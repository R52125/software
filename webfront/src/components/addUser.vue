<template>
  <div>
    <br>
    <br>
    <br>
    <div class="card ml-5 mr-5 text-center">
      <div class="card-header">
        <h4>用户信息处理</h4>
      </div>
      <div class="card-body ml-3 mr-3">
        <br>
        <div class="input-group mb-3">
          <div class="input-group-prepend">
            <span class="input-group-text" id="basic-addon1">房间号</span>
          </div>
          <input type="text" class="form-control" placeholder="RoomId" aria-label="Username" aria-describedby="basic-addon1" v-model="Room_id">
        </div>
        <br>
        <div class="input-group mb-3">
          <div class="input-group-prepend">
            <span class="input-group-text" id="basic-addon2">身份证号</span>
          </div>
          <input type="text" class="form-control" placeholder="UserId" aria-label="Username" aria-describedby="basic-addon1" v-model="user_id">
        </div>
        <div>
          <br>
          <a class="btn btn-success mr-5 ml-5" role="button" @click="addNewUser">用户登记</a>
          <a class="btn btn-success mr-5 ml-5" role="button" @click="reduceUser">用户退房</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {mapMutations, mapState} from "vuex";

export default {
  name: "addUser",
  computed:{
    ...mapState['Room_id', 'user_id'],
    Room_id:{
      get(){
        return this.$store.state.Room_id;
      },
      set(value){
        return this.$store.commit('handleRoomId', value);
      }
    },
    user_id:{
      get(){
        return this.$store.state.user_id;
      },
      set(value) {
        return this.$store.commit('handleUserId', value);
      }
    }
  },
  methods:{
    ...mapMutations(['addnewuser', 'reduceuser']),
    addNewUser(){
      this.addnewuser()
    },
    reduceUser(){
      this.reduceuser()
    }
  }
}
</script>

<style scoped>

</style>