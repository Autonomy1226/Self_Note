<template>
  <li>
    <span v-if="!editing" @dblclick="editing = true">{{ folder.name }}</span>
    <input v-else v-model="editName" @blur="saveEdit" @keyup.enter="saveEdit" />
    <button @click="delFolder">删除</button>
    <ul v-if="folder.children && folder.children.length">
      <FolderNode v-for="child in folder.children" :key="child.id" :folder="child" @refresh="$emit('refresh')" />
    </ul>
  </li>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';
const props = defineProps({ folder: Object });
const emit = defineEmits(['refresh']);
const editing = ref(false);
const editName = ref(props.folder.name);
const user = JSON.parse(localStorage.getItem('user') || '{}');
const isLogin = !!user.id;

const saveEdit = async () => {
  if (editName.value && editName.value !== props.folder.name) {
    if (isLogin) {
      await axios.put('/api/folders', { ...props.folder, name: editName.value, userId: user.id });
      emit('refresh');
    } else {
      let guestFolders = JSON.parse(localStorage.getItem('guest_folders') || '[]');
      guestFolders = guestFolders.map(f => f.id === props.folder.id ? { ...f, name: editName.value } : f);
      localStorage.setItem('guest_folders', JSON.stringify(guestFolders));
      emit('refresh');
    }
  }
  editing.value = false;
};
const delFolder = async () => {
  if (isLogin) {
    await axios.delete(`/api/folders/${props.folder.id}`, { params: { userId: user.id } });
    emit('refresh');
  } else {
    let guestFolders = JSON.parse(localStorage.getItem('guest_folders') || '[]');
    guestFolders = guestFolders.filter(f => f.id !== props.folder.id);
    localStorage.setItem('guest_folders', JSON.stringify(guestFolders));
    emit('refresh');
  }
};
</script> 