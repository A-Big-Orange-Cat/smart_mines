// 参数类型
const PARAM_TYPE_LIST = [
  {
    label: "参数设置",
    value: 1,
  },
  {
    label: "操作指令",
    value: 2,
  },
  {
    label: "信号",
    value: 3,
  },
  {
    label: "配置",
    value: 4,
  },
];
// 参数值类型
const VALUE_TYPE_LIST = [
  {
    label: "bool",
    value: 1,
  },
  {
    label: "int",
    value: 2,
  },
];
// 通信协议
const COMMUNICATION_PROTOCOL_LIST = [
  {
    label: 'OPC',
    value: 1
  },
  {
    label: 'Modbus TCP',
    value: 2
  },
  {
    label: 'S7',
    value: 3
  }
]
// 参数设置

// 图表颜色
const CHART_COLOR = ['#73c0de', '#5470c6', '#6dadf8', '#91cc75', '#3ba272',
'#9a60b4', '#ffff00', '#e9fa58', '#ffa500', '#fc5252', '#5d5deb']

export {PARAM_TYPE_LIST, VALUE_TYPE_LIST, CHART_COLOR}
