<template>
<div>
  <div class="card shadow-lg ml-5 mr-5 mt-3 mb-3" v-for="data in monitorlist">
    <div class="card-body">
      <div>
        <br>
        <h3>房间号：{{ data.Room_id }}</h3>
<!--        <div>{{cstate}}</div>-->
<!--        <div>{{Rmode}}</div>-->
<!--        <div>{{tem}}</div>-->
<!--        <div>{{wind}}</div>-->
        <div v-if="data.cstate == 1">
          <br>
          <h5>空调开机状态：已开机</h5>
          <br>
          <div v-if="data.Rmode == 0">
            <h5>空调模式：制冷</h5>
          </div>
          <div v-else>
            <h5>空调模式：供暖</h5>
          </div>
          <br>
          <h5>房间温度：{{ data.temp }}℃</h5>
          <br>
          <div v-if="data.wind == 0">
            <h5>空调风速：低</h5>
          </div>
          <div v-else-if="data.wind == 1">
            <h5>空调风速：中</h5>
          </div>
          <div v-else="data.wind == 2">
            <h5>空调风速：高</h5>
          </div>
          <br>
          <div v-if="data.ss == 0">
            <h5>空调送风状态：不送风</h5>
          </div>
          <div v-else>
            <h5>空调送风状态：送风</h5>
          </div>
        </div>
        <div v-if="data.cstate == 0">
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
    ...mapState(['Room_id', 'monitorlist']),
  }
}
</script>

<style scoped>

</style>