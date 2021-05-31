<template>
  <div>
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
      <a class="navbar-brand">管理员界面</a>
      <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>

      <div class="collapse navbar-collapse" id="navbarSupportedContent">
        <ul class="navbar-nav mr-auto">
<!--          <li class="nav-item active">-->
<!--            <a class="nav-link" href="#/master/CentralSwitch">中央空调控制-->
<!--              <span class="sr-only">(current)</span>-->
<!--            </a>-->
<!--          </li>-->
          <li class="nav-item">
            <a class="nav-link" href="#/master/adduser">用户登记</a>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="#/master/CentralConfigs">中央空调配置</a>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="#/master/centralmonitoroption">监测房间温度</a>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="#/master/formoption">报表信息</a>
          </li>
        </ul>
        <ul class="ml-auto">
          <li class="nav-item dropdown ml-auto">
            <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-toggle="dropdown" aria-expanded="false">
              其他
            </a>
            <div class="dropdown-menu" aria-labelledby="navbarDropdown">
              <a class="dropdown-item" href="#/master/mastermes">管理员信息</a>
              <a class="dropdown-item" href="#">退出登录</a>
              <!--            <div class="dropdown-divider"></div>-->
            </div>
          </li>
        </ul>

<!--        <form class="form-inline my-2 my-lg-0">-->
<!--          <input class="form-control mr-sm-2" type="search" placeholder="Search" aria-label="Search">-->
<!--          <button class="btn btn-outline-success my-2 my-sm-0" type="submit">Search</button>-->
<!--        </form>-->
      </div>
    </nav>
    <router-view></router-view>
  </div>
</template>

<script>
export default {
  name: "Navigation"
}
</script>

<style scoped>

</style>