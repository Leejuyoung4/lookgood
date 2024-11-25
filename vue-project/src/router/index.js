import { createRouter, createWebHistory } from 'vue-router';
import MainView from '@/views/MainView.vue'; // 홈 페이지 컴포넌트
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
import LoginViewModal from '@/views/LoginViewModal.vue';    // 모달 컴포넌트 import
import SignupViewModal from '@/views/SignupViewModal.vue';  // 모달 컴포넌트 import
import SearchResultView from '@/views/SearchResultView.vue';

const routes = [
  { 
    path: '/', 
    name: 'main', 
    component: MainView 
  },
  {
    path: '/videos',
    name: 'videos',
    component: VideosView
  },
  {
    path: '/videos/:id',
    name: 'VideoDetail',
    component: () => import('@/views/VideosDetailView.vue')
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
{
  path: '/login',
  name: 'login',
  component: LoginViewModal
},
{
  path: '/signup',
  name: 'signup',
  component: SignupViewModal
},
{ 
  path: '/search', 
  name: 'search', 
  component: SearchResultView 
},


];

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    // 항상 페이지 상단으로 이동
    return { top: 0 };
  },
});

export default router;
