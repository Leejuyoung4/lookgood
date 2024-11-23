import { createRouter, createWebHistory } from 'vue-router';
import MainView from '@/views/MainView.vue'; // 홈 페이지 컴포넌트
import LoginView from '@/views/LoginView.vue'; // 로그인 페이지 컴포넌트
import SignupView from '@/views/SignupView.vue'; // 회원가입 페이지 컴포넌트
import VideosView from '@/views/VideosView.vue';
import EventView from '@/views/EventView.vue';
import EventDetailView from '@/views/EventDetailView.vue';
import VideosDetailView from '@/views/VideosDetailView.vue';
import GroupView from '@/views/GroupView.vue';
import GroupDetailView from '@/views/GroupDetailView.vue';
import UpdateGroupView from '@/views/UpdateGroupView.vue'
import InquiryView from '@/views/InquiryView.vue';
import InquiryDetailView from '@/views/InquiryDetailView.vue';
import UpdateInquiryView from '@/views/UpdateInquiryView.vue';
import MyPage from '@/views/MyPage.vue'


const routes = [
  { 
    path: '/', 
    name: 'main', 
    component: MainView 
  },

  { 
    path: '/login', 
    name: 'login', 
    component: LoginView 
  },
  { 
    path: '/signup', 
    name: 'signup', 
    component: SignupView 
  },
  {
    path: '/videos',
    name: 'videos',
    component: VideosView
  },
  {
    path: '/videos/:id',
    name: 'video-detail',
    component: VideosDetailView,
    props: true
  },
  {
    path: '/event',
    name: 'event',
    component: EventView
  },
  {
    path: '/event/detail/:id',  // URL 패턴 설정
    name: 'EventDetail',        // 라우트 이름
    component: EventDetailView,     // 사용할 컴포넌트
    props: true             
  },
  {
    path: '/community/group',
    name: 'group',
    component: GroupView
  },
  {
    path: '/community/group/detail/:gBoardNo',
    name: 'groupDetail',
    component: GroupDetailView
  },
  {
    path: '/community/group/edit/:gBoardNo',
    name: 'updateGroupDetail',
    component: UpdateGroupView
  },
  	
 {
  path: '/community/inquiry',
  name: 'inquiry',
  component: InquiryView
},
{
  path: '/community/inquiries/detail/:iNo',
  name: 'inquiriesDetail',
  component: InquiryDetailView
},
{
  path: '/community/inquiries/edit/:iNo',
  name: 'updateInquiriesDetail',
  component: UpdateInquiryView
},
{
  path: '/mypage',
  name: 'mypage',
  component: MyPage,
  meta: { requiresAuth: true }  // 로그인 필요
},



];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
