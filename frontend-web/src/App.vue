<template>
  <router-view />
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+SC:wght@300;400;500;600;700&display=swap');

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body, #app {
  height: 100%;
  width: 100%;
}

#app {
  font-family: 'Noto Sans SC', -apple-system, BlinkMacSystemFont, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2D3436;
  line-height: 1.6;
}

a {
  text-decoration: none;
  color: #6B9AC4;
}

a:hover {
  text-decoration: underline;
}
</style>
