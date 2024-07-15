import http from './http'
import { Message } from 'element-ui'

//写入设备值
export const deviceWrite = async (type, code, val, deviceTypeId, deviceId) => {
  var codesAndValues = {}
  codesAndValues[code] = val
  var url = type == 'command' ? '/monitor/updateParameterValues' : '/monitor/remoteOrLocal'
  try {
    const res = await http.post(url, {
      deviceTypeId,
      deviceId,
      codesAndValues
    })
    if (res.data.code == 200) {
      if (res.data.data[code].success) {
        Message({
          message: '【' + res.data.data[code].parameterName + '】' + res.data.data[code].message,
          type: 'success'
        });
      } else {
        Message({
          message: '【' + res.data.data[code].parameterName + '】' + res.data.data[code].message,
          type: 'error'
        });
      }
    } else {
      Message({
        message: res.data.msg,
        type: 'error'
      });
    }
  } catch (error) {
    console.log(error);
  }
}
