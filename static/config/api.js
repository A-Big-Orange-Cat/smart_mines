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
    } else if (res.data.code != 401) {
      Message({
        message: res.data.msg,
        type: 'error'
      });
    }
  } catch (error) {
    console.log(error);
  }
}

// 公共API调用
export const callApi = async ( f ) => {
  console.log('ffff', f);
  let res = f()
  console.log('res', res);
  // if(res.code == 200){
  //   return res.data
  // } else if (res.data.code != 401 ) {
  //   Message({
  //     message: res.data.msg,
  //     type: 'error'
  //   });
  // }
}


// 获取信息
export async function getList(params) {
  try {
    return await http.post('/getList', params)
  } catch (error) {
    console.log(error);
  }
}

function filterResult(res) {
  if (res.data.code == 200) {
    return res.data.data
  } else if (res.data.code != 401) {
    Message({
      message: res.data.msg,
      type: 'error'
    });
  }
}


