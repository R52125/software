<template>
  <div>
    <br>
    <br>
    <div v-if="onoff == 0" style="text-align: center" >
      <i class="bi bi-power mr-2 ml-2 btn" @click="centralswitch">
        <svg xmlns="http://www.w3.org/2000/svg" width="35" height="35" fill="currentColor" class="bi bi-power rounded-circle bg-danger" viewBox="-7 -7 30 30">
          <path d="M7.5 1v7h1V1h-1z"/>
          <path d="M3 8.812a4.999 4.999 0 0 1 2.578-4.375l-.485-.874A6 6 0 1 0 11 3.616l-.501.865A5 5 0 1 1 3 8.812z"/>
        </svg>
      </i>
    </div>
    <div v-else style="text-align: center" >
      <i class="bi bi-power mr-2 ml-2 btn" @click="centralswitch">
        <svg xmlns="http://www.w3.org/2000/svg" width="35" height="35" fill="currentColor" class="bi bi-power rounded-circle bg-success" viewBox="-7 -7 30 30">
          <path d="M7.5 1v7h1V1h-1z"/>
          <path d="M3 8.812a4.999 4.999 0 0 1 2.578-4.375l-.485-.874A6 6 0 1 0 11 3.616l-.501.865A5 5 0 1 1 3 8.812z"/>
        </svg>
      </i>
    </div>
    <br>
    <div class="card shadow-lg ml-5 mr-5" v-if="onoff == 0">
      <div class="card-body ml-5 mr-5">
        <br>
        <div class="input-group mb-3">
          <div class="input-group-prepend">
            <label class="input-group-text" for="inputGroupSelect01">工作模式选择</label>
          </div>
          <select class="form-control" id="inputGroupSelect01" v-model="mode">
            <option selected value=0>制冷模式</option>
            <option value=1>供暖模式</option>
          </select>
        </div>
        <br>
        <div class="input-group mb-3">
          <div class="input-group-prepend">
            <label class="input-group-text" for="inputGroupSelect01">缺省工作温度</label>
          </div>
          <template v-if="mode == 0">
            <input type="text" class="form-control" id="fnge1" value="22">
          </template>
          <template v-else>
            <input type="text" class="form-control" id="fnge2" value="28">
          </template>

        </div>
        <br>
        <div class="input-group mb-3">
          <div class="input-group-prepend">
            <span class="input-group-text">刷新频率</span>
          </div>
          <input type="number" class="form-control" aria-label="Amount (to the nearest dollar)" v-model="frequency">
          <div class="input-group-append">
            <span class="input-group-text">ms</span>
          </div>
        </div>
        <br>
        <div class="text-center">
          <input class="btn btn-primary mr-5 ml-5" type="submit" value="重置">
          <input class="btn btn-primary mr-5 ml-5" type="submit" value="提交" @click="send">
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {mapState, mapMutations, mapActions} from 'vuex'
export default {
  name: "CentralConfigs",
  data: function (){
    return{
    }
  },
  computed:{
    ...mapState(['mode', 'frequency', 'onoff']),
    mode:{
      get(){
        return this.$store.state.mode;
      },
      set(value){
        this.$store.commit('handlemode', value);
      }
    },
    frequency:{
      get(){
        return this.$store.state.frequency;
      },
      set(value){
        this.$store.commit('handlefre', value);
      }
    }
  },
  methods:{
    ...mapMutations(['sendconfig', 'switchreq']),
    send(){
      this.sendconfig()
    },
    centralswitch(){
      this.switchreq()
    }
  },

}
</script>

<style scoped>

</style>