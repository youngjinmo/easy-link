<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script lang="ts" setup>
// App component setup
</script>

<style lang="scss">
html, body {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  height: 100%;
}

#app {
  font-family: 'Helvetica Neue', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  height: 100%;
}
</style>
