import { createRouter, createWebHistory } from 'vue-router';

const routes = [
  { path: '/', component: () => import('../pages/Home.vue'),
    children: [
      { path: '', redirect: '/notes' },
      { path: 'notes', component: () => import('../pages/NoteList.vue') },
      { path: 'note/:id', component: () => import('../pages/NoteDetail.vue') },
      { path: 'folders', component: () => import('../pages/FolderList.vue') },
      { path: 'tags', component: () => import('../pages/TagList.vue') },
      { path: 'stats', component: () => import('../pages/Stats.vue') },
      { path: 'profile', component: () => import('../pages/Profile.vue') },
    ]
  },
  { path: '/login', component: () => import('../pages/Login.vue') },
  { path: '/register', component: () => import('../pages/Register.vue') },
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router; 