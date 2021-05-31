<template>
<div>
  <div class="card shadow-lg ml-5 mr-5">
    <div class="card-body">
      <div>
        <br>
        <h3>房间号：{{ Room_id }}</h3>
<!--        <div>{{cstate}}</div>-->
<!--        <div>{{Rmode}}</div>-->
<!--        <div>{{tem}}</div>-->
<!--        <div>{{wind}}</div>-->
        <div v-if="cstate == 1">
          <br>
          <h5>空调开机状态：已开机</h5>
          <br>
          <div v-if="Rmode == 0">
            <h5>空调模式：制冷</h5>
          </div>
          <div v-else>
            <h5>空调模式：供暖</h5>
          </div>
          <br>
          <h5>房间温度：{{ tem }}℃</h5>
          <br>
          <div v-if="wind == 0">
            <h5>空调风速：低</h5>
          </div>
          <div v-else-if="wind == 1">
            <h5>空调风速：中</h5>
          </div>
          <div v-else="wind == 2">
            <h5>空调风速：高</h5>
          </div>
        </div>
        <div v-if="cstate == 0">
          <br>
          <h5>空调开机状态：未开机</h5>
        </div>
      </div>
    </div>
  </div>
</div>
</template>

<script>
import {mapState} from "vuex";

export default {
  name: "CentralMonitor",
  computed:{
    ...mapState(['Room_id', 'cstate', 'Rmode', 'tem', 'wind']),
  }
}
</script>

<style scoped>

</style>