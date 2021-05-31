<template>
  <div class="container-fluid">
    <b-jumbotron>
      <template v-slot:header>分布式温控系统</template>
      <template v-slot:lead>
        响应节能，绿色环保
      </template>
      <hr class="my-4">
    </b-jumbotron>
<!--    <div>-->
<!--      hello-->
<!--      {{Ctemp}}-->
<!--      {{Cmode}}-->
<!--    </div>-->
    <b-tabs content-class="mt-3">
      <b-tab title="普通用户">
        <b-card bg-variant="light">
          <div>
            <form>
              <div class="form-group">
                <b-form-input id="input-1" placeholder="请输入房间号" v-model="roomnumber"></b-form-input>
                <small id="userNameHelpBlock" class="form-text text-muted" style="text-align: left">
                  房间号只包含数字，4位数
                </small>
                <br>
                <b-form-input id="input-1" placeholder="请输入身份证号" v-model="username"></b-form-input>
                <small id="userPasswordHelpBlock" class="form-text text-muted" style="text-align: left">
                  身份证号为18位
                </small>
              </div>
            </form>
          </div>
<!--          <b-button variant="primary" href="#/user"> 普通用户登录</b-button>-->
          <b-button variant="primary" @click="login"> 普通用户登录</b-button>
        </b-card>

      </b-tab>

    </b-tabs>
  </div>

</template>

<script>
import {mapState, mapMutations} from 'vuex'

export default {
  name: "UserLogin",
  data: function (){
    return{
      username: '',
      roomnumber: '',
    };
  },
  computed:{
    ...mapState(['Ctemp', 'Cmode']),
  },
  // 方法
  methods: {
    ...mapMutations(['check_id']),
    login(){
      this.check_id({
        username: this.username,
        roomnumber: this.roomnumber
      });
    },
  },
}
</script>

<style scoped>

</style>