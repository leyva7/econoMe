<template>
  <div class="top-bar">
    <div class="left-content">
      <span class="accounting-name">{{ accountingName }}</span>
    </div>
    <div class="user-actions-container">
      <div class="user-actions">
        <span class="username">{{username}}</span>
        <img src="../assets/icons/user.svg" alt="User Icon" class="user-icon">
      </div>
      <div class="tooltip">
        <a href="#" @click="navigate('/modify-details')">Modificar usuario</a>
        <a href="#" @click="navigate('/modify-password')">Modificar contraseña</a>
        <a href="#" @click="navigate('/')">Cerrar sesión</a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useAccountingStore } from '@/stores/accountingStore';

const { username, accountingName, navigate} = useAccountingStore();


</script>

<style scoped>
  .top-bar {
    display: flex;
    justify-content: space-between; /* Ajusta los elementos a los extremos */
    padding: 40px;
    background-color: rgb(236, 240, 241);
    border-radius: 25px;
    color: #2C3E50;
    font-size: 16px;
    font-weight: bold;
  }

  .left-content {
    /* Estilos para el contenido izquierdo (nombre de la contabilidad) */
    display: flex;
    align-items: center;
  }

  .accounting-name{
    font-size: x-large;
  }

  .user-actions-container {
    /* Ajustes para alinear los elementos de usuario a la derecha */
    display: flex;
    align-items: center;
  }

  .user-actions-container, .user-actions {
    position: relative;
    display: inline-block;
  }

  .user-actions{
    display: flex;
    align-items: center;
  }

  .username {
    margin-right: 10px;
    color: #2C3E50;
    font-size: larger;
    font-weight: bold;
  }

  .user-icon {
    width: 50px;
    height: auto;
    margin-right: 15px;
  }

  .tooltip {
    position: absolute;
    top: 100%;
    left: 50%;
    transform: translateX(-50%);
    background-color: #FFFFFF;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    z-index: 100;
    padding: 10px;
    border-radius: 5px;
    opacity: 0;
    visibility: hidden;
    transition: opacity 0.5s, visibility 0.5s;
  }

  .user-actions-container:hover .tooltip, .user-actions:hover .tooltip {
    opacity: 1;
    visibility: visible;
  }

  .tooltip a {
    display: block;
    color: #2C3E50;
    text-align: center;
    text-decoration: none;
    margin: 5px 0;
  }

  .user-actions-container:hover .tooltip, .user-actions:hover .tooltip {
    display: block;
  }
</style>