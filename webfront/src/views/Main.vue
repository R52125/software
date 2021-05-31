
  <template>
    <div class="container-fluid">
      <b-jumbotron>
        <template v-slot:header>分布式温控系统</template>
        <template v-slot:lead>
          响应节能，绿色环保
        </template>
        <hr class="my-4">
      </b-jumbotron>

      <b-tabs content-class="mt-3 ml-5 mr-5">
        <b-tab variant="primary" title="管理员" active>
          <b-card bg-variant="light">
            <div>
              <form>
                <div class="form-group">
                  <b-form-input id="input-1" placeholder="请输入管理员账号" v-model="master"></b-form-input>
                  <small id="adminNameHelpBlock" class="form-text text-muted">
                    用户名10位，只包含数字
                  </small>
                  <br>
                  <b-form-input id="input-1" placeholder="请输入密码" v-model="masterpassword"></b-form-input>
                  <small id="passwordHelpBlock" class="form-text text-muted">
                    密码长度为6-18位
                  </small>
                </div>
              </form>
            </div>
            <b-button variant="primary" @click="login">管理员登录</b-button>
          </b-card>
        </b-tab>

      </b-tabs>
    </div>


  </template>

<script>

import {mapState, mapMutations, mapActions} from "vuex";

export default {
  name: 'Main',
  data: function (){
    return{}
  },
  computed:{
    ...mapState['master', 'masterpassword'],
    master:{
      get(){
        return this.$store.state.master;
      },
      set(value){
        this.$store.commit('handle_master', value);
      }
    },
    masterpassword: {
      get(){
        return this.$store.state.masterpassword;
      },
      set(value) {
        this.$store.commit('handle_masterpassword', value);
      }
    }
  },
  methods:{
    ...mapMutations(['check_id']),
    login(){
      this.check_id()
    }
  }
}
</script>
