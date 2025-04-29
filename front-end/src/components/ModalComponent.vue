<template>
  <div class="modal-overlay" @click="$emit('close')">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <button class="close-button" @click="$emit('close')">&times;</button>
      </div>
      <div class="modal-body">
        <p>{{ message }}</p>
      </div>
      <div class="modal-footer">
        <button class="modal-button" @click="$emit('close')">Got it</button>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
defineProps<{
  message: string;
}>();

defineEmits<{
  (e: 'close'): void;
}>();
</script>

<style lang="scss" scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.modal-header {
  padding: 10px 15px;
  display: flex;
  justify-content: flex-end;
  border-bottom: 1px solid #eee;
}

.modal-body {
  padding: 20px;
  text-align: center;
}

.modal-footer {
  padding: 10px 15px;
  display: flex;
  justify-content: center;
  border-top: 1px solid #eee;
}

.close-button {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #888;
  
  &:hover {
    color: #333;
  }
}

.modal-button {
  padding: 8px 16px;
  background-color: #4a90e2;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  
  &:hover {
    background-color: #3a7bc8;
  }
}
</style> 