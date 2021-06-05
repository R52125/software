<template>
  <div>
    <div class="text-center">
      {{test1}}
    </div>
    <h1 class="text-center mb-5">日报表</h1>

    <div class="card mr-5 ml-5 shadow-lg mb-5">
      <div class="card-header text-center">
        <h4>房间号：{{ Room_id }}</h4>
        <p class="card-text">从控机开关机次数：{{ up_times }}</p>
      </div>
      <div class="card-body mr-5 ml-5" v-for="index in days" :key="index">
        <div class="text-center" v-if="formmodel == 0">
          {{formArray[index-1]}}
        </div>
        <div class="text-center" v-else-if="formmodel == 1">
          {{formArray[index-1]}} ~ {{ formendweek[index-1] }}
        </div>
        <div class="text-center" v-else>
          {{formArray[index-1]}}
        </div>
<!--        <div>-->
<!--          <h2>{{index}}</h2>-->
<!--          <h3>{{form_count[index-1]}}</h3>-->
<!--        </div>-->
        <div class="table-responsive pagination">
          <table class="table table-striped">
            <thead>
            <tr>
              <th scope="col">#</th>
              <th scope="col">开启时间</th>
              <th scope="col">关闭时间</th>
              <th scope="col">初始温度</th>
              <th scope="col">目标温度</th>
              <th scope="col">风量消耗</th>
              <th scope="col">电量消耗</th>
              <th scope="col">所需费用</th>
            </tr>
            </thead>
            <tbody>
<!--            <div>{{rem_i}}</div>-->
            <tr v-for="i in form_count[index-1]">
<!--              <div>i: {{ i }}</div>-->
<!--              <div>i: {{ i }}</div>-->
<!--              <div>i: {{i}}</div>-->
<!--              <div>index-1: {{index-1}}</div>-->
<!--              <div>form_location[index-1]: {{form_location[index-2]}}</div>-->
<!--              <div>{{formdatalist[rem_i]}}</div>-->
              <th scope="row">{{ formdatalist[form_location[index-1]+i-1].id }}</th>
              <td>{{ formdatalist[form_location[index-1]+i-1].start_time }}</td>
              <td>{{ formdatalist[form_location[index-1]+i-1].stop_time }}</td>
              <td>{{ formdatalist[form_location[index-1]+i-1].start_temp }}℃</td>
              <td>{{ formdatalist[form_location[index-1]+i-1].end_temp }}℃</td>
              <td>{{ formdatalist[form_location[index-1]+i-1].wind_power }}L</td>
              <td>{{ formdatalist[form_location[index-1]+i-1].electricity }}°</td>
              <td>￥{{ formdatalist[form_location[index-1]+i-1].cost }}</td>
            </tr>
            </tbody>
          </table>
        </div>
        <p v-if="formmodel == 0">每日所需总费用：{{form_cost_circletotal[index-1]}}</p>
        <p v-else-if="formmodel == 1">每周所需总费用：{{form_cost_circletotal[index-1]}}</p>
        <p v-else>每月所需总费用：￥{{form_cost_circletotal[index-1]}}</p>
      </div>
      <div class="card-footer bg-transparent border-success text-center">所需总费用：￥{{total_cost}}</div>
    </div>


  </div>
</template>

<script>
import {mapState} from "vuex";

export default {
  name: "Formmes",
  data: function() {
    return{
    }
  },
  computed:{
    ...mapState(['Room_id', 'up_times', 'total_cost','days', 'formdatalist', 'form_count', 'form_location', 'test1', "formArray", 'formmodel', 'formendweek', 'form_cost_circletotal']),
  },
  methods:{
  },
  // 自动触发点击事件
  directives:{
    trigger:{
      inserted(el,binging){
        // console.log("自动触发事件")
        el.click()
      }
    }
  },
}
</script>

<style scoped>

</style>