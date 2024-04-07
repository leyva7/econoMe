<template>
  <div class="container-fluid vh-100">
    <div class="row h-100">
      <div class="col-md-6 d-flex align-items-center justify-content-center bg-custom-blue-500 text-black">
        <AppDescription />
      </div>
      <div class="col-md-6 d-flex align-items-center justify-content-center bg-custom-blue-300">
        <UserLogin />
      </div>
    </div>
  </div>
</template>


<script>
  import AppDescription from '../components/AppDescription.vue';
  import UserLogin from '../components/UserLogin.vue';

  
  export default {
    name: 'App',
    components: {
      AppDescription,
      UserLogin,
    },
  };
  </script>
  
  <style>
  
  </style>