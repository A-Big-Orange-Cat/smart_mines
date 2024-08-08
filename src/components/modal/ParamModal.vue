<template>
  <el-dialog title="参数设置" class="custom-modal" :visible="visible" :width="modalWidth" :modal="false"
    :append-to-body="true" :close-on-click-modal="false" @update:visible="updateVisible">
    <!-- 风门 -->
    <el-form :ref="deviceType" class="form" v-if="deviceType == 'damper'" :model="damper_form" label-position="left">
      <el-form-item label="风门延时参数" prop="door_delay">
        <el-input-number class="inputNumber" v-model="damper_form.door_delay" controls-position="right"
          :step-strictly="true" :min="0" :max="999" placeholder="请输入风门延时参数（最大999）"></el-input-number>
        <span class="form-unit">秒</span>
      </el-form-item>
      <el-form-item label="防夹延时参数" prop="pinch_delay">
        <el-input-number class="inputNumber" v-model="damper_form.pinch_delay" controls-position="right"
          :step-strictly="true" :min="0" :max="999" placeholder="请输入防夹延时参数（最大999）"></el-input-number>
        <span class="form-unit">秒</span>
      </el-form-item>
      <el-form-item label="电机延时参数" prop="motor_delay">
        <el-input-number class="inputNumber" v-model="damper_form.motor_delay" controls-position="right"
          :step-strictly="true" :min="0" :max="999" placeholder="请输入电机延时参数（最大999）"></el-input-number>
        <span class="form-unit">秒</span>
      </el-form-item>
      <el-form-item label="传感器延时" prop="sensor_delay">
        <el-input-number class="inputNumber" v-model="damper_form.sensor_delay" controls-position="right"
          :step-strictly="true" :min="0" :max="999" placeholder="请输入传感器延时（最大999）"></el-input-number>
        <span class="form-unit">毫秒</span>
      </el-form-item>
      <el-form-item label="两道门同时打开" prop="type">
        <el-select v-model="damper_form.type" placeholder="请选择类型" style="width: 100%">
          <el-option label="禁止" value="false"></el-option>
          <el-option label="允许" value="true"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="控制方式" prop="control_mode">
        <el-select v-model="damper_form.control_mode" placeholder="请选择控制方式" style="width: 100%">
          <el-option label="计数" value="1"></el-option>
          <el-option label="延时" value="0"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="传感器信号方式" prop="sensor_mode">
        <el-select v-model="damper_form.sensor_mode" placeholder="请选择传感器信号方式" style="width: 100%">
          <el-option label="持续" value="1"></el-option>
          <el-option label="脉冲" value="0"></el-option>
        </el-select>
      </el-form-item>
      <!-- <el-form-item label="风窗角度参数" prop="angle">
            <el-input v-model="damper_form.angle" type="number" min="0" placeholder="请输入风窗角度参数"></el-input>
          </el-form-item>
          <el-form-item label="风窗风速参数" prop="wind_speed">
            <el-input v-model="damper_form.wind_speed" type="number" min="0" placeholder="请输入风窗风速参数"></el-input>
          </el-form-item> -->
    </el-form>
    <!-- 风窗-双 -->
    <el-form :ref="deviceType" class="form" v-if="deviceType == 'wind'" :model="wind_form" label-position="left">
      <el-form-item label="设置A风窗角度" prop="a_wind_angle">
        <el-input v-model="wind_form.a_wind_angle" type="number" min="0" placeholder="请输入A风窗角度"></el-input>
      </el-form-item>
      <el-form-item label="设置B风窗角度" prop="b_wind_angle">
        <el-input v-model="wind_form.b_wind_angle" type="number" min="0" placeholder="请输入B风窗角度"></el-input>
      </el-form-item>
      <el-form-item label="设置A风窗自动" prop="a_is_automatic">
        <el-select v-model="wind_form.a_is_automatic" placeholder="请选择类型" style="width: 100%">
          <el-option label="手动" value="false"></el-option>
          <el-option label="自动" value="true"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="设置B风窗自动" prop="b_is_automatic">
        <el-select v-model="wind_form.b_is_automatic" placeholder="请选择类型" style="width: 100%">
          <el-option label="手动" value="false"></el-option>
          <el-option label="自动" value="true"></el-option>
        </el-select>
      </el-form-item>
    </el-form>
    <!-- 风窗-单 -->
    <el-form :ref="deviceType" class="form" v-if="deviceType == 'windOne'" :model="wind_form" label-position="left">
      <el-form-item label="设置风窗角度" prop="wind_angle">
        <el-input v-model="wind_one_form.wind_angle" type="number" min="0" placeholder="请输入风窗角度"></el-input>
      </el-form-item>
      <el-form-item label="设置风窗自动" prop="is_automatic">
        <el-select v-model="wind_one_form.is_automatic" placeholder="请选择类型" style="width: 100%">
          <el-option label="手动" value="false"></el-option>
          <el-option label="自动" value="true"></el-option>
        </el-select>
      </el-form-item>
    </el-form>
    <!-- 精准测风 -->
    <el-form :ref="deviceType" class="form-measureWind" v-if="deviceType == 'measureWind'" :model="measureWind_form"
      label-position="left" :hide-required-asterisk="true">
      <el-form-item label="定时测风" prop="is_time">
        <el-select v-model="measureWind_form.is_time" placeholder="请选择测风类型" style="width: 100%">
          <el-option label="实时测风" value="0"></el-option>
          <el-option label="定时测风" value="1"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="截面面积" prop="area">
        <el-input v-model="measureWind_form.area" type="number" min="0" placeholder="请输入截面面积"></el-input>
      </el-form-item>
      <el-form-item label="时间" class="form-time" v-show="measureWind_form.is_time == '1'">
        <el-row class="item-row" type="flex" justify="space-between" v-for="(item, index) in measureWind_form.times" :key="index">
          <el-col :span="11">
            <el-form-item class="col-form-item" :label="`小时${index + 1}`" :prop="`times.${index}.hour`">
              <el-input v-model="item.hour" type="number" placeholder="请输入小时"
                min="0" max="23"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="11">
            <el-form-item class="col-form-item" :label="`分钟${index + 1}`" :prop="`times.${index}.minute`">
              <el-input v-model="item.minute" type="number" placeholder="请输入分钟"
                min="0" max="59"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="1" class="col-icon"></el-col>
        </el-row>
      </el-form-item>
    </el-form>
    <!-- 大巷 转载点 合称为 多功能喷雾 -->
    <el-form :ref="deviceType" class="form-mainLane" v-if="deviceType == 'mainLane'" :model="mainLane_form"
      label-position="left" :hide-required-asterisk="true">
      <el-form-item label="喷雾延时" prop="spray_delay">
        <el-input-number class="inputNumber" v-model="mainLane_form.spray_delay" controls-position="right"
          :step-strictly="true" placeholder="请输入喷雾延时（最大999）" :min="0" :max="999"></el-input-number>
      </el-form-item>
      <el-form-item label="粉尘浓度上限" prop="dust">
        <el-input-number class="inputNumber" v-model="mainLane_form.dust" controls-position="right"
          :step-strictly="true" placeholder="请输入粉尘浓度上限（最大1000）" :min="0" :max="1000"></el-input-number>
      </el-form-item>
      <el-form-item label="喷雾功能模式" prop="type">
        <el-select v-model="mainLane_form.type" placeholder="请选择喷雾功能模式" style="width: 100%">
          <el-option label="大巷定时" value="0"></el-option>
          <el-option label="大巷模式" value="1"></el-option>
          <el-option label="转载点" value="2"></el-option>
          <el-option label="防尘防火" value="3"></el-option>
          <el-option label="防火" value="4"></el-option>
          <el-option label="水电联动" value="5"></el-option>
          <el-option label="防尘红外" value="6"></el-option>
          <el-option label="粉尘喷雾" value="7"></el-option>
          <el-option label="防火红外" value="8"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="定时列表" class="form-time" v-show="mainLane_form.type == 0">
        <el-row class="item-row" v-for="(item, index) in mainLane_form.times" :key="index">
          <el-col :span="2">
            {{ `${index+1}` }}
          </el-col>
          <el-col :span="5">
            <el-form-item class="form-item-time" :prop="`times.${index}.start_hour`">
              <el-input class="input-time" v-model="item.start_hour" type="number" :min="0" :max="23"></el-input>
              <span>时</span>
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item class="form-item-time" :prop="`times.${index}.start_minute`">
              <el-input class="input-time" v-model="item.start_minute" type="number" :min="0" :max="59"></el-input>
              <span>分</span>
            </el-form-item>
          </el-col>
          <el-col :span="2" style="text-align: left;">
            <span>&nbsp;—</span>
          </el-col>
          <el-col :span="5">
            <el-form-item class="form-item-time" :prop="`times.${index}.end_hour`">
              <el-input class="input-time" v-model="item.end_hour" type="number" :min="0" :max="23"></el-input>
              <span>时</span>
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item class="form-item-time" :prop="`times.${index}.end_minute`">
              <el-input class="input-time" v-model="item.end_minute" type="number" :min="0" :max="59"></el-input>
              <span>分</span>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form-item>
    </el-form>
    <!-- 综采喷雾 -->
    <el-form :ref="deviceType" class="form form_mine" v-if="deviceType == 'mine'" :model="mine_form"
      label-position="left">
      <el-row type="flex" justify="space-between">
        <el-col :span="11">
          <el-form-item label="跟踪喷雾同喷" prop="miner_spray">
            <el-input-number class="inputNumber" v-model="mine_form.miner_spray" controls-position="right"
              :step-strictly="true" placeholder="请输入跟踪喷雾同喷（最大9）" :min="0" :max="9"></el-input-number>
          </el-form-item>
          <el-form-item label="跟踪喷雾间隔" prop="miner_space">
            <el-input-number class="inputNumber" v-model="mine_form.miner_space" controls-position="right"
              :step-strictly="true" placeholder="请输入跟踪喷雾间隔（最大9）" :min="0" :max="9"></el-input-number>
          </el-form-item>
          <el-form-item label="跟踪喷雾延时" prop="miner_delay">
            <el-input-number class="inputNumber" v-model="mine_form.miner_delay" controls-position="right"
              :step-strictly="true" placeholder="请输入跟踪喷雾延时（最大999）" :min="0" :max="999"></el-input-number>
          </el-form-item>
          <el-form-item label="移架喷雾同喷" prop="frame_spray">
            <el-input-number class="inputNumber" v-model="mine_form.frame_spray" controls-position="right"
              :step-strictly="true" placeholder="请输入移架喷雾同喷（最大9）" :min="0" :max="9"></el-input-number>
          </el-form-item>
          <el-form-item label="移架喷雾间隔" prop="frame_space">
            <el-input-number class="inputNumber" v-model="mine_form.frame_space" controls-position="right"
              :step-strictly="true" placeholder="请输入移架喷雾间隔（最大9）" :min="0" :max="9"></el-input-number>
          </el-form-item>
          <el-form-item label="移架喷雾延时" prop="frame_delay">
            <el-input-number class="inputNumber" v-model="mine_form.frame_delay" controls-position="right"
              :step-strictly="true" placeholder="请输入移架喷雾延时（最大999）" :min="0" :max="999"></el-input-number>
          </el-form-item>
        </el-col>
        <el-col :span="11">
          <el-form-item label="放煤喷雾上道同喷" prop="coal_spray">
            <el-input-number class="inputNumber" v-model="mine_form.coal_spray" controls-position="right"
              :step-strictly="true" placeholder="请输入放煤喷雾上道同喷（最大9）" :min="0" :max="9"></el-input-number>
          </el-form-item>
          <el-form-item label="放煤喷雾下道同喷" prop="coal_space">
            <el-input-number class="inputNumber" v-model="mine_form.coal_space" controls-position="right"
              :step-strictly="true" placeholder="请输入放煤喷雾下道同喷（最大9）" :min="0" :max="9"></el-input-number>
          </el-form-item>
          <el-form-item label="放煤喷雾延时" prop="coal_delay">
            <el-input-number class="inputNumber" v-model="mine_form.coal_delay" controls-position="right"
              :step-strictly="true" placeholder="请输入放煤喷雾延时（最大999）" :min="0" :max="999"></el-input-number>
          </el-form-item>
          <el-form-item label="粉尘浓度1上限" prop="dust_1" v-show="dustNumber >= 1">
            <el-input-number class="inputNumber" v-model="mine_form.dust_1" controls-position="right"
              :step-strictly="true" placeholder="请输入粉尘浓度上限" :min="0" :max="1000"></el-input-number>
          </el-form-item>
          <el-form-item label="粉尘浓度2上限" prop="dust_2" v-show="dustNumber >= 2">
            <el-input-number class="inputNumber" v-model="mine_form.dust_2" controls-position="right"
              :step-strictly="true" placeholder="请输入粉尘浓度上限" :min="0" :max="1000"></el-input-number>
          </el-form-item>
          <el-form-item label="粉尘浓度3上限" prop="dust_3" v-show="dustNumber == 3">
            <el-input-number class="inputNumber" v-model="mine_form.dust_3" controls-position="right"
              :step-strictly="true" placeholder="请输入粉尘浓度上限" :min="0" :max="1000"></el-input-number>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <!-- 风门带风窗 - 风门参数设置 -->
    <el-form :ref="deviceType" class="form" v-if="deviceType == 'damperWindow'" :model="damper_window_form" label-position="left">
      <el-form-item label="风门延时参数" prop="door_delay">
        <el-input-number class="inputNumber" v-model="damper_window_form.door_delay" controls-position="right"
          :step-strictly="true" :min="0" :max="999" placeholder="请输入风门延时参数（最大999）"></el-input-number>
        <span class="form-unit">秒</span>
      </el-form-item>
      <el-form-item label="防夹延时参数" prop="pinch_delay">
        <el-input-number class="inputNumber" v-model="damper_window_form.pinch_delay" controls-position="right"
          :step-strictly="true" :min="0" :max="999" placeholder="请输入防夹延时参数（最大999）"></el-input-number>
        <span class="form-unit">秒</span>
      </el-form-item>
      <el-form-item label="电机延时参数" prop="motor_delay">
        <el-input-number class="inputNumber" v-model="damper_window_form.motor_delay" controls-position="right"
          :step-strictly="true" :min="0" :max="999" placeholder="请输入电机延时参数（最大999）"></el-input-number>
        <span class="form-unit">秒</span>
      </el-form-item>
      <el-form-item label="传感器延时" prop="sensor_delay">
        <el-input-number class="inputNumber" v-model="damper_window_form.sensor_delay" controls-position="right"
          :step-strictly="true" :min="0" :max="999" placeholder="请输入传感器延时（最大999）"></el-input-number>
        <span class="form-unit">毫秒</span>
      </el-form-item>
      <el-form-item label="两道门同时打开" prop="type">
        <el-select v-model="damper_window_form.type" placeholder="请选择类型" style="width: 100%">
          <el-option label="禁止" value="false"></el-option>
          <el-option label="允许" value="true"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="控制方式" prop="control_mode">
        <el-select v-model="damper_window_form.control_mode" placeholder="请选择控制方式" style="width: 100%">
          <el-option label="计数" value="1"></el-option>
          <el-option label="延时" value="0"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="传感器信号方式" prop="sensor_mode">
        <el-select v-model="damper_window_form.sensor_mode" placeholder="请选择传感器信号方式" style="width: 100%">
          <el-option label="持续" value="1"></el-option>
          <el-option label="脉冲" value="0"></el-option>
        </el-select>
      </el-form-item>
    </el-form>
    <!-- 风门带风窗 - 风窗参数设置 - 百叶 -->
    <el-form :ref="deviceType" class="form" v-if="deviceType == 'louvre'" :model="window_form" label-position="left">
      <el-form-item label="设置风窗角度" prop="wind_angle">
        <el-input v-model="window_form.wind_angle" type="number" min="0" placeholder="请输入风窗角度"></el-input>
      </el-form-item>
      <el-form-item label="设置风窗自动" prop="is_automatic">
        <el-select v-model="window_form.is_automatic" placeholder="请选择类型" style="width: 100%">
          <el-option label="手动" value="false"></el-option>
          <el-option label="自动" value="true"></el-option>
        </el-select>
      </el-form-item>
    </el-form>
    <!-- 风门带风窗 - 风窗参数设置 - 推拉 -->
    <el-form :ref="deviceType" class="form" v-if="deviceType == 'slide'" :model="slide_form" label-position="left">
      <el-form-item label="设置风窗长度" prop="wind_length">
        <el-input v-model="slide_form.wind_length" type="number" min="0" placeholder="请输入风窗长度">
        </el-input>
      </el-form-item>
    </el-form>
    <!-- 副井口风门 -->
    <el-form :ref="deviceType" class="form" v-if="deviceType == 'pithead'" :model="pithead_form" label-position="left">
      <el-form-item label="风门延时参数" prop="door_delay">
        <el-input-number class="inputNumber" v-model="pithead_form.door_delay" controls-position="right"
          :step-strictly="true" :min="0" :max="999" placeholder="请输入风门延时参数（最大999）"></el-input-number>
        <span class="form-unit">秒</span>
      </el-form-item>
      <el-form-item label="防夹延时参数" prop="pinch_delay">
        <el-input-number class="inputNumber" v-model="pithead_form.pinch_delay" controls-position="right"
          :step-strictly="true" :min="0" :max="999" placeholder="请输入防夹延时参数（最大999）"></el-input-number>
        <span class="form-unit">秒</span>
      </el-form-item>
      <el-form-item label="电机延时参数" prop="motor_delay">
        <el-input-number class="inputNumber" v-model="pithead_form.motor_delay" controls-position="right"
          :step-strictly="true" :min="0" :max="999" placeholder="请输入电机延时参数（最大999）"></el-input-number>
        <span class="form-unit">秒</span>
      </el-form-item>
      <el-form-item label="传感器延时" prop="sensor_delay">
        <el-input-number class="inputNumber" v-model="pithead_form.sensor_delay" controls-position="right"
          :step-strictly="true" :min="0" :max="999" placeholder="请输入传感器延时（最大999）"></el-input-number>
        <span class="form-unit">毫秒</span>
      </el-form-item>
    </el-form>
    <div slot="footer">
      <el-button @click="updateVisible(false)">取 消</el-button>
      <el-button type="primary" @click="submit">保 存</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { VALUE_TYPE_LIST, PARAM_TYPE_LIST } from '../../../static/config/constant'
export default {
  name: "ParamModal",
  props: {
    visible: Boolean,
    deviceType: String,
    paramsList: Array,
    windowIndex: [Number, String],
    dustNumber: Number // 粉尘浓度个数
  },
  data() {
    return {
      modalWidth: '30%',
      damper_form: {
        door_delay: "",
        pinch_delay: "",
        motor_delay: "",
        sensor_delay: "",
        type: "",
        // angle: "",
        // wind_speed: "",
        control_mode: "",
        sensor_mode: ""
      },
      wind_form: {
        a_wind_angle: undefined,
        b_wind_angle: undefined,
        a_is_automatic: undefined,
        b_is_automatic: undefined,
      },
      wind_one_form: {
        wind_angle: undefined,
        is_automatic: undefined,
      },
      measureWind_form: {
        is_time: undefined,
        area: undefined,
        times: [
          {
            hour: undefined,
            minute: undefined
          },
          {
            hour: undefined,
            minute: undefined
          },
          {
            hour: undefined,
            minute: undefined
          },
          {
            hour: undefined,
            minute: undefined
          },
          {
            hour: undefined,
            minute: undefined
          },
          {
            hour: undefined,
            minute: undefined
          },
          {
            hour: undefined,
            minute: undefined
          },
          {
            hour: undefined,
            minute: undefined
          },
          {
            hour: undefined,
            minute: undefined
          },
          {
            hour: undefined,
            minute: undefined
          }
        ]
      },
      mainLane_form: {
        spray_delay: undefined,
        dust: undefined,
        type: '',
        times: [
          {
            start_hour: undefined,
            start_minute: undefined,
            end_hour: undefined,
            end_minute: undefined,
          },
          {
            start_hour: undefined,
            start_minute: undefined,
            end_hour: undefined,
            end_minute: undefined,
          },
          {
            start_hour: undefined,
            start_minute: undefined,
            end_hour: undefined,
            end_minute: undefined,
          },
          {
            start_hour: undefined,
            start_minute: undefined,
            end_hour: undefined,
            end_minute: undefined,
          },
          {
            start_hour: undefined,
            start_minute: undefined,
            end_hour: undefined,
            end_minute: undefined,
          },
          {
            start_hour: undefined,
            start_minute: undefined,
            end_hour: undefined,
            end_minute: undefined,
          },
          {
            start_hour: undefined,
            start_minute: undefined,
            end_hour: undefined,
            end_minute: undefined,
          },
          {
            start_hour: undefined,
            start_minute: undefined,
            end_hour: undefined,
            end_minute: undefined,
          },
          {
            start_hour: undefined,
            start_minute: undefined,
            end_hour: undefined,
            end_minute: undefined,
          },
          {
            start_hour: undefined,
            start_minute: undefined,
            end_hour: undefined,
            end_minute: undefined,
          },
        ]
      },
      mine_form: {
        miner_spray: undefined,
        miner_space: undefined,
        miner_delay: undefined,
        frame_spray: undefined,
        frame_space: undefined,
        frame_delay: undefined,
        coal_spray: undefined,
        coal_space: undefined,
        coal_delay: undefined,
        dust_1: undefined,
        dust_2: undefined,
        dust_3: undefined
      },
      damper_window_form: {
        door_delay: "",
        pinch_delay: "",
        motor_delay: "",
        sensor_delay: "",
        type: "",
        control_mode: "",
        sensor_mode: ""
      },
      window_form: {
        wind_angle: undefined,
        is_automatic: undefined,
      },
      slide_form: {
        wind_length: undefined,
      },
      pithead_form: {
        door_delay: "",
        pinch_delay: "",
        motor_delay: "",
        sensor_delay: "",
      },
      paramsData: [],
      deviceParameters: [],
    };
  },
  watch: {
    'deviceType': {
      handler(val) {
        if (val == 'mine') {
          this.modalWidth = '45%';
        } else if (val == 'measureWind' || val == 'mainLane') {
          this.modalWidth = '40%';
        } else {
          this.modalWidth = '30%';
        }
      },
      immediate: true
    },
    'visible': {
      handler(val) {
        if (val) {
          this.queryParaSettingValues()
        }
      }
    }
  },
  methods: {
    updateVisible(newVisible) {
      this.$refs[this.deviceType].resetFields();
      this.$emit("update:visible", newVisible);
    },
    submit() {
      this.updateParaSettingValues()
    },

    // 按设备类型id和设备id查询参数设置的接口
    async queryParaSettingValues() {
      try {
        const res = await this.$http.post('/monitor/queryParaSettingValues', {
          deviceId: Number(this.$route.params.deviceId),
          deviceTypeId: Number(this.$route.params.deviceTypeId)
        })
        this.paramsData = res.data.data;
        switch (this.deviceType) {
          case 'damper':
            this.damper_form.door_delay = this.paramsData.FM_CS_FMYS
            this.damper_form.pinch_delay = this.paramsData.FM_CS_FJYS
            this.damper_form.motor_delay = this.paramsData.FM_CS_DJYS
            this.damper_form.sensor_delay = this.paramsData.FM_CS_CGQYS
            this.damper_form.type = this.paramsData.FM_CS_TK
            this.damper_form.control_mode = this.paramsData.FM_CS_KZFS
            this.damper_form.sensor_mode = this.paramsData.FM_CS_CGQXHFS
            break;

          case 'wind':
            this.wind_form.a_wind_angle = this.paramsData.FC_CS_JD1
            this.wind_form.b_wind_angle = this.paramsData.FC_CS_JD2
            this.wind_form.a_is_automatic = this.paramsData.FC_CS_ZD1
            this.wind_form.b_is_automatic = this.paramsData.FC_CS_ZD2
            break;

          case 'windOne':
            this.wind_one_form.wind_angle = this.paramsData[41]
            this.wind_one_form.is_automatic = this.paramsData[74]
            break;

          case 'measureWind':
            this.measureWind_form.is_time = this.paramsData[96]
            this.measureWind_form.area = this.paramsData[95]
            this.measureWind_form.times.map((item, index) => {
              item.hour = this.paramsData[index * 2 + 97]
              item.minute = this.paramsData[index * 2 + 98]
            })
            break;

          case 'mine':
            this.mine_form.miner_spray = this.paramsData.ZC_CS_GZTP
            this.mine_form.miner_space = this.paramsData.ZC_CS_GZJG
            this.mine_form.miner_delay = this.paramsData.ZC_CS_GZYS
            this.mine_form.frame_spray = this.paramsData.ZC_CS_YJTP
            this.mine_form.frame_space = this.paramsData.ZC_CS_YJJG
            this.mine_form.frame_delay = this.paramsData.ZC_CS_YJYS
            this.mine_form.coal_spray = this.paramsData.ZC_CS_FMSDTP
            this.mine_form.coal_space = this.paramsData.ZC_CS_FMXDTP
            this.mine_form.coal_delay = this.paramsData.ZC_CS_FMYS
            this.mine_form.dust_1 = this.paramsData.ZC_CS_FCNDSX1
            this.mine_form.dust_2 = this.paramsData.ZC_CS_FCNDSX2
            this.mine_form.dust_3 = this.paramsData.ZC_CS_FCNDSX3
            break;

          case 'mainLane':
            this.mainLane_form.spray_delay = this.paramsData.PW_CS_PWYS
            this.mainLane_form.dust = this.paramsData.PW_CS_FCNDSX
            this.mainLane_form.type = this.paramsData.PW_CS_PWGNMS
            this.mainLane_form.times.map((item, index) => {
              item.start_hour = this.paramsData[`PW_CS_STARTH${index + 1}`]
              item.start_minute = this.paramsData[`PW_CS_STARTM${index + 1}`]
              item.end_hour = this.paramsData[`PW_CS_STOPH${index + 1}`]
              item.end_minute = this.paramsData[`PW_CS_STOPM${index + 1}`]
            })
            break;

          case 'damperWindow':
            this.damper_window_form.door_delay = this.paramsData.FM_CS_FMYS
            this.damper_window_form.pinch_delay = this.paramsData.FM_CS_FJYS
            this.damper_window_form.motor_delay = this.paramsData.FM_CS_DJYS
            this.damper_window_form.sensor_delay = this.paramsData.FM_CS_CGQYS
            this.damper_window_form.type = this.paramsData.FM_CS_TK
            this.damper_window_form.control_mode = this.paramsData.FM_CS_KZFS
            this.damper_window_form.sensor_mode = this.paramsData.FM_CS_CGQXHFS
            break;

          case 'louvre':
            this.window_form.wind_angle = this.paramsData[`FC_CS_JD${this.windowIndex}`]
            this.window_form.is_automatic = this.paramsData[`FC_CS_ZD${this.windowIndex}`]
            break;

          case 'slide':
            this.slide_form.wind_length = this.paramsData[`FC_CS_CD${this.windowIndex}`]

          case 'pithead':
            this.pithead_form.door_delay = this.paramsData.FFM_CS_FMYS
            this.pithead_form.pinch_delay = this.paramsData.FFM_CS_FJYS
            this.pithead_form.motor_delay = this.paramsData.FFM_CS_DJYS
            this.pithead_form.sensor_delay = this.paramsData.FFM_CS_CGQYS
            break;

          default:
            break;
        }

      } catch (error) {
        console.log(error);
      }
    },

    // 按设备类型id和设备id更新设备的参数设置的值接口
    async updateParaSettingValues() {
      var obj = {}
      switch (this.deviceType) {
        case 'damper':
          if(this.damper_form.door_delay != this.paramsData.FM_CS_FMYS) {
            obj.FM_CS_FMYS = this.damper_form.door_delay
          }
          if (this.damper_form.pinch_delay != this.paramsData.FM_CS_FJYS) {
            obj.FM_CS_FJYS = this.damper_form.pinch_delay
          }
          if (this.damper_form.motor_delay != this.paramsData.FM_CS_DJYS) {
            obj.FM_CS_DJYS = this.damper_form.motor_delay
          }
          if (this.damper_form.sensor_delay != this.paramsData.FM_CS_CGQYS) {
            obj.FM_CS_CGQYS = this.damper_form.sensor_delay
          }
          if (this.damper_form.type != this.paramsData.FM_CS_TK) {
            obj.FM_CS_TK = this.damper_form.type
          }
          if (this.damper_form.control_mode != this.paramsData.FM_CS_KZFS) {
            obj.FM_CS_KZFS = this.damper_form.control_mode
          }
          if (this.damper_form.sensor_mode != this.paramsData.FM_CS_CGQXHFS) {
            obj.FM_CS_CGQXHFS = this.damper_form.sensor_mode
          }
          break;

        case 'wind':
          if(this.wind_form.a_wind_angle != this.paramsData.FC_CS_JD1) {
            obj.FC_CS_JD1 = this.wind_form.a_wind_angle
          }
          if (this.wind_form.b_wind_angle != this.paramsData.FC_CS_JD2) {
            obj.FC_CS_JD2 = this.wind_form.b_wind_angle
          }
          if (this.wind_form.a_is_automatic != this.paramsData.FC_CS_ZD1) {
            obj.FC_CS_ZD1 = this.wind_form.a_is_automatic
          }
          if (this.wind_form.b_is_automatic != this.paramsData.FC_CS_ZD2) {
            obj.FC_CS_ZD2 = this.wind_form.b_is_automatic
          }
          break;

        case 'windOne':
          if(this.wind_one_form.wind_angle != this.paramsData[41]) {
            this.compareParameter(41, this.wind_one_form.wind_angle)
          }
          if (this.wind_one_form.is_automatic != this.paramsData[74]) {
            this.compareParameter(74, this.wind_one_form.is_automatic)
          }
          break;

        case 'measureWind':
          if(this.measureWind_form.is_time != this.paramsData[96]) {
            this.compareParameter(96, this.measureWind_form.is_time)
          }
          if (this.measureWind_form.area != this.paramsData[95]) {
            this.compareParameter(95, this.measureWind_form.area)
          }
          this.measureWind_form.times.map((item, index) => {
            if (item.hour != this.paramsData[index * 2 + 97]) {
              this.compareParameter((index * 2 + 97), item.hour)
            }
            if (item.minute != this.paramsData[index * 2 + 98]) {
              this.compareParameter((index * 2 + 98), item.minute)
            }
          })
          break;

        case 'mine':
          if(this.mine_form.miner_spray != this.paramsData.ZC_CS_GZTP) {
            obj.ZC_CS_GZTP = this.mine_form.miner_spray
          }
          if(this.mine_form.miner_space != this.paramsData.ZC_CS_GZJG) {
            obj.ZC_CS_GZJG = this.mine_form.miner_space
          }
          if(this.mine_form.miner_delay != this.paramsData.ZC_CS_GZYS) {
            obj.ZC_CS_GZYS = this.mine_form.miner_delay
          }
          if(this.mine_form.frame_spray != this.paramsData.ZC_CS_YJTP) {
            obj.ZC_CS_YJTP = this.mine_form.frame_spray
          }
          if(this.mine_form.frame_space != this.paramsData.ZC_CS_YJJG) {
            obj.ZC_CS_YJJG = this.mine_form.frame_space
          }
          if(this.mine_form.frame_delay != this.paramsData.ZC_CS_YJYS) {
            obj.ZC_CS_YJYS = this.mine_form.frame_delay
          }
          if(this.mine_form.coal_spray != this.paramsData.ZC_CS_FMSDTP) {
            obj.ZC_CS_FMSDTP = this.mine_form.coal_spray
          }
          if(this.mine_form.coal_space != this.paramsData.ZC_CS_FMXDTP) {
            obj.ZC_CS_FMXDTP = this.mine_form.coal_space
          }
          if(this.mine_form.coal_delay != this.paramsData.ZC_CS_FMYS) {
            obj.ZC_CS_FMYS = this.mine_form.coal_delay
          }
          if(this.mine_form.dust_1 != this.paramsData.ZC_CS_FCNDSX1) {
            obj.ZC_CS_FCNDSX1 = this.mine_form.dust_1
          }
          if(this.mine_form.dust_2 != this.paramsData.ZC_CS_FCNDSX2) {
            obj.ZC_CS_FCNDSX2 = this.mine_form.dust_2
          }
          if(this.mine_form.dust_3 != this.paramsData.ZC_CS_FCNDSX3) {
            obj.ZC_CS_FCNDSX3 = this.mine_form.dust_3
          }
          break;

        case 'mainLane':
          if(this.mainLane_form.spray_delay != this.paramsData.PW_CS_PWYS) {
            obj.PW_CS_PWYS = this.mainLane_form.spray_delay
          }
          if (this.mainLane_form.dust != this.paramsData.PW_CS_FCNDSX) {
            obj.PW_CS_FCNDSX = this.mainLane_form.dust
          }
          if (this.mainLane_form.type != this.paramsData.PW_CS_PWGNMS) {
            obj.PW_CS_PWGNMS = this.mainLane_form.type
          }
          this.mainLane_form.times.map((item, index) => {
            if (item.start_hour != this.paramsData[`PW_CS_STARTH${index + 1}`]) {
              obj[`PW_CS_STARTH${index + 1}`] = item.start_hour
            }
            if (item.start_minute != this.paramsData[`PW_CS_STARTM${index + 1}`]) {
              obj[`PW_CS_STARTM${index + 1}`] = item.start_minute
            }
            if (item.end_hour != this.paramsData[`PW_CS_STOPH${index + 1}`]) {
              obj[`PW_CS_STOPH${index + 1}`] = item.end_hour
            }
            if (item.end_minute != this.paramsData[`PW_CS_STOPM${index + 1}`]) {
              obj[`PW_CS_STOPM${index + 1}`] = item.end_minute
            }
          })
          break;

        case 'damperWindow':
          if(this.damper_window_form.door_delay != this.paramsData.FM_CS_FMYS) {
            obj.FM_CS_FMYS = this.damper_window_form.door_delay
          }
          if (this.damper_window_form.pinch_delay != this.paramsData.FM_CS_FJYS) {
            obj.FM_CS_FJYS = this.damper_window_form.pinch_delay
          }
          if (this.damper_window_form.motor_delay != this.paramsData.FM_CS_DJYS) {
            obj.FM_CS_DJYS = this.damper_window_form.motor_delay
          }
          if (this.damper_window_form.sensor_delay != this.paramsData.FM_CS_CGQYS) {
            obj.FM_CS_CGQYS = this.damper_window_form.sensor_delay
          }
          if (this.damper_window_form.type != this.paramsData.FM_CS_TK) {
            obj.FM_CS_TK = this.damper_window_form.type
          }
          if (this.damper_window_form.control_mode != this.paramsData.FM_CS_KZFS) {
            obj.FM_CS_KZFS = this.damper_window_form.control_mode
          }
          if (this.damper_window_form.sensor_mode != this.paramsData.FM_CS_CGQXHFS) {
            obj.FM_CS_CGQXHFS = this.damper_window_form.sensor_mode
          }
          break;

        case 'louvre':
          if(this.window_form.wind_angle != this.paramsData[`FC_CS_JD${this.windowIndex}`]) {
            obj[`FC_CS_JD${this.windowIndex}`] = this.window_form.wind_angle
          }
          if (this.window_form.is_automatic != this.paramsData[`FC_CS_ZD${this.windowIndex}`]) {
            obj[`FC_CS_ZD${this.windowIndex}`] = this.window_form.is_automatic
          }
          break;

        case 'slide':
          if(this.slide_form.wind_length != this.paramsData[`FC_CS_CD${this.windowIndex}`]) {
            obj[`FC_CS_CD${this.windowIndex}`] = this.slide_form.wind_length
          }
          break;

        case 'pithead':
          if(this.pithead_form.door_delay != this.paramsData.FFM_CS_FMYS) {
            obj.FFM_CS_FMYS = this.pithead_form.door_delay
          }
          if (this.pithead_form.pinch_delay != this.paramsData.FFM_CS_FJYS) {
            obj.FFM_CS_FJYS = this.pithead_form.pinch_delay
          }
          if (this.pithead_form.motor_delay != this.paramsData.FFM_CS_DJYS) {
            obj.FFM_CS_DJYS = this.pithead_form.motor_delay
          }
          if (this.pithead_form.sensor_delay != this.paramsData.FFM_CS_CGQYS) {
            obj.FFM_CS_CGQYS = this.pithead_form.sensor_delay
          }
          break;

        default:
          break;
      }
      if (JSON.stringify(obj) != '{}') {
        try {
          const res = await this.$http.post('/monitor/updateParameterValues', {
            deviceId: Number(this.$route.params.deviceId),
            deviceTypeId: Number(this.$route.params.deviceTypeId),
            codesAndValues: obj
          })
          if (res.data.code == 200) {
            let flag = true;
            for (let key in res.data.data) {
              if (!res.data.data[key].success) {
                this.$message.error('【' + res.data.data[key].parameterName + '】' + res.data.data[key].message)
                return
              }
            }
            if (flag) {
              this.$message.success('参数保存成功')
              this.updateVisible(false)
            }
          } else {
            this.$message.error(res.data.msg)
          }
        } catch (error) {
          console.log(error);
        }
      } else {
        this.$message.warning('没有要保存的参数')
      }
    },

  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.custom-modal {
  display: flex;
  align-items: center;
  justify-content: center;
}
.custom-modal >>> .el-dialog {
  margin: auto !important;
}
.custom-modal >>> .el-dialog__body {
  padding: 24px;
}
.custom-modal >>> .el-dialog__header {
  text-align: center;
}

.form >>> .el-form-item__label {
  width: 160px;
  color: #333;
  font-size: 14px; /*no*/
}
.form >>> .el-form-item__content {
  margin-left: 160px;
  text-align: left;
  display: flex;
}
/* 精准测风 */
.form-measureWind >>> .el-form-item__label {
  width: 90px;
  color: #333;
  font-size: 14px; /*no*/
}
.form-measureWind >>> .el-form-item__content {
  margin-left: 90px;
  text-align: left;
  display: flex;
}

.form-time {
  margin-bottom: 0;
}
.form-time >>> .el-form-item__content {
  max-height: 360px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}
.form-time >>> .el-form-item__content::-webkit-scrollbar {
  width: 6px;
  background-color: rgba(50,50,50,0.2);
}
.form-time >>> .el-form-item__content::-webkit-scrollbar-thumb {
  background-color: rgba(50,50,50,0.25);
  border-radius: 3px;
}

.col-form-item >>> .el-form-item__label {
  width: 70px;
  color: #333;
  font-size: 14px; /*no*/
}
.col-form-item >>> .el-form-item__content {
  margin-left: 70px;
  font-size: 14px; /*no*/
  max-height: none;
  overflow-y: visible;
  display: flex;
}

.item-tip {
  margin-left: 12px;
  font-size: 12px; /*no*/
}

.item-row {
  margin-bottom: 24px;
}

.col-icon {
  font-size: 24px;
}
.col-icon > i {
  cursor: pointer;
}

.form-mainLane >>> .el-form-item__label {
  width: 120px;
  color: #333;
  font-size: 14px; /*no*/
}
.form-mainLane >>> .el-form-item__content {
  margin-left: 120px;
  text-align: left;
  display: flex;
}

.form-item-time >>> .el-form-item__content {
  margin-left: 0;
  max-height: none;
  overflow-y: visible;
  display: inline-block;
}
.input-time {
  width: 90px;
}

.form_mine >>> .el-form-item {
  margin-bottom: 16px
}

.inputNumber {
  width: 100%;
}
.inputNumber >>> .el-input__inner {
  text-align: left;
}

.form-unit {
  display: inline-block;
  width: 60px;
  padding-left: 12px;
  text-align: left;
  box-sizing: border-box;
}
</style>
