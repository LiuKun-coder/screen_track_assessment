package org.example.sc_backend.service.impl;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import org.example.sc_backend.common.exception.BusinessException;
import org.example.sc_backend.service.SmsCodeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SmsCodeServiceImpl implements SmsCodeService {

    private static final Set<String> SUPPORTED_TYPES = Set.of("register", "reset", "login");
    private static final SecureRandom RANDOM = new SecureRandom();

    private final Map<String, SmsCodeEntry> codeCache = new ConcurrentHashMap<>();

    @Value("${sms.aliyun.access-key:}")
    private String accessKey;

    @Value("${sms.aliyun.access-key-secret:}")
    private String accessKeySecret;

    @Value("${sms.aliyun.endpoint:dysmsapi.aliyuncs.com}")
    private String endpoint;

    @Value("${sms.aliyun.sign-name:}")
    private String signName;

    @Value("${sms.aliyun.template-code:}")
    private String templateCode;

    @Value("${sms.code-expire-seconds:300}")
    private Integer codeExpireSeconds;

    @Value("${sms.resend-interval-seconds:60}")
    private Integer resendIntervalSeconds;

    @Override
    public void sendCode(String phone, String type) {
        String normalizedPhone = normalizePhone(phone);
        String normalizedType = normalizeType(type);
        validateSmsConfig();

        String cacheKey = buildCacheKey(normalizedPhone, normalizedType);
        SmsCodeEntry existing = codeCache.get(cacheKey);
        LocalDateTime now = LocalDateTime.now();
        if (existing != null && existing.nextSendTime.isAfter(now)) {
            long waitSeconds = java.time.Duration.between(now, existing.nextSendTime).getSeconds();
            throw new BusinessException(429, "发送过于频繁，请 " + Math.max(waitSeconds, 1) + " 秒后重试");
        }

        String code = generateCode();
        sendSmsByAliyun(normalizedPhone, code);

        codeCache.put(cacheKey, new SmsCodeEntry(
                code,
                now.plusSeconds(codeExpireSeconds),
                now.plusSeconds(resendIntervalSeconds)
        ));
    }

    @Override
    public void validateCode(String phone, String type, String code) {
        String normalizedPhone = normalizePhone(phone);
        String normalizedType = normalizeType(type);
        if (code == null || code.isBlank()) {
            throw new BusinessException(400, "验证码不能为空");
        }

        String cacheKey = buildCacheKey(normalizedPhone, normalizedType);
        SmsCodeEntry entry = codeCache.get(cacheKey);
        if (entry == null) {
            throw new BusinessException(400, "验证码不存在或已失效");
        }

        if (entry.expireTime.isBefore(LocalDateTime.now())) {
            codeCache.remove(cacheKey);
            throw new BusinessException(400, "验证码已过期");
        }

        if (!entry.code.equals(code.trim())) {
            throw new BusinessException(400, "验证码错误");
        }

        codeCache.remove(cacheKey);
    }

    private void sendSmsByAliyun(String phone, String code) {
        try {
            Config config = new Config()
                    .setAccessKeyId(accessKey)
                    .setAccessKeySecret(accessKeySecret)
                    .setEndpoint(endpoint);

            Client client = new Client(config);
            String templateParam = "{\"code\":\"" + code + "\"}";

            SendSmsRequest request = new SendSmsRequest()
                    .setPhoneNumbers(phone)
                    .setSignName(signName)
                    .setTemplateCode(templateCode)
                    .setTemplateParam(templateParam);

            SendSmsResponse response = client.sendSms(request);
            String responseCode = response.getBody() == null ? null : response.getBody().getCode();
            if (!"OK".equalsIgnoreCase(responseCode)) {
                String message = response.getBody() == null ? "短信发送失败" : response.getBody().getMessage();
                throw new BusinessException(500, "短信发送失败: " + message);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(500, "短信发送失败，请稍后重试");
        }
    }

    private void validateSmsConfig() {
        if (isBlank(accessKey) || isBlank(accessKeySecret) || isBlank(signName) || isBlank(templateCode)) {
            throw new BusinessException(500, "短信服务未配置，请检查阿里云短信参数");
        }
    }

    private String normalizePhone(String phone) {
        if (phone == null || !phone.matches("^1[3-9]\\d{9}$")) {
            throw new BusinessException(400, "手机号格式不正确");
        }
        return phone;
    }

    private String normalizeType(String type) {
        String normalized = type == null ? "" : type.trim().toLowerCase();
        if (!SUPPORTED_TYPES.contains(normalized)) {
            throw new BusinessException(400, "验证码类型不支持");
        }
        return normalized;
    }

    private String generateCode() {
        int value = RANDOM.nextInt(900000) + 100000;
        return String.valueOf(value);
    }

    private String buildCacheKey(String phone, String type) {
        return type + ":" + phone;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static class SmsCodeEntry {
        private final String code;
        private final LocalDateTime expireTime;
        private final LocalDateTime nextSendTime;

        private SmsCodeEntry(String code, LocalDateTime expireTime, LocalDateTime nextSendTime) {
            this.code = code;
            this.expireTime = expireTime;
            this.nextSendTime = nextSendTime;
        }
    }
}
