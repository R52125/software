<template>
  <div>
    <h1 class="text-center mb-5">日报表</h1>

    <div class="card mr-5 ml-5 shadow-lg mb-5">
      <div class="card-header text-center">
        <h4>房间号：{{ Room_id }}</h4>
        <p class="card-text">从控机开关机次数：{{ up_times }}</p>
      </div>
      <div class="card-body mr-5 ml-5">
        <div></div>
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
            <tr v-for="data in formdatalist" :key="data.id">
              <th scope="row">{{ data.id }}</th>
              <td>{{ data.start_time }}</td>
              <td>{{ data.stop_time }}</td>
              <td>{{ data.start_temp }}℃</td>
              <td>{{ data.end_temp }}℃</td>
              <td>{{ data.wind_power }}L</td>
              <td>{{ data.electricity }}°</td>
              <td>￥{{ data.cost }}</td>
            </tr>
            </tbody>
          </table>
        </div>
        <p>每日所需总费用：{{total_cost}}</p>
      </div>
    </div>

<!--    <div class="card mr-5 ml-5 shadow-lg">-->
<!--      <div class="card-header text-center">-->
<!--        <h4>房间号：2</h4>-->
<!--      </div>-->
<!--      <div class="card-body mr-5 ml-5">-->
<!--        <p class="card-text">从控机开关机次数：5</p>-->
<!--        <div class="table-responsive pagination">-->
<!--          <table class="table table-striped">-->
<!--            <thead>-->
<!--            <tr>-->
<!--              <th scope="col">#</th>-->
<!--              <th scope="col">开启时间</th>-->
<!--              <th scope="col">关闭时间</th>-->
<!--              <th scope="col">初始温度</th>-->
<!--              <th scope="col">目标温度</th>-->
<!--              <th scope="col">风量消耗</th>-->
<!--              <th scope="col">所需费用</th>-->
<!--            </tr>-->
<!--            </thead>-->
<!--            <tbody>-->
<!--            <tr>-->
<!--              <th scope="row">1</th>-->
<!--              <td>2021.05.20 9:20</td>-->
<!--              <td>2021.05.20 9:21</td>-->
<!--              <td>23℃</td>-->
<!--              <td>25℃</td>-->
<!--              <td>100L</td>-->
<!--              <td>￥100</td>-->
<!--            </tr>-->
<!--            <tr>-->
<!--              <th scope="row">2</th>-->
<!--              <td>Jacob</td>-->
<!--              <td>Thornton</td>-->
<!--              <td>@fat</td>-->
<!--            </tr>-->
<!--            <tr>-->
<!--              <th scope="row">3</th>-->
<!--              <td>Larry</td>-->
<!--              <td>the Bird</td>-->
<!--              <td>@twitter</td>-->
<!--            </tr>-->
<!--            </tbody>-->
<!--          </table>-->
<!--        </div>-->
<!--      </div>-->
<!--    </div>-->

  </div>
</template>

<script>
import {mapState} from "vuex";

export default {
  name: "Formmes",
  computed:{
    ...mapState(['Room_id', 'up_times', 'total_cost', 'formdatalist']),
  }
}
</script>

<style scoped>

</style>