export function createPath(url, path) {
  if (url.charAt(url.length - 1) === "/") {
    url = url.substr(0, url.length - 1);
  }
  if (path.charAt(0) === "/") {
    return `${url}${path}`;
  }
  return `${url}/${path}`;
}


export const fetchApi = (apiPath, body, headers) => {
  return new Promise(function(resolve, reject) {
    fetch(apiPath, {
      method: "GET",
      headers: headers,
      body: body,
      cache: "no-cache",
    }).then(response => {
      return new Promise((httpResolve, httpReject) => {
        let status = response.status;
        if (status === 200) {
          response.json().then(rs => {
            httpResolve({ ...rs, responseStatusCode: status });
          }).catch(_ => {
            httpReject({ code: -1, note: "系统异常，请稍候重试！", data: {}, responseStatusCode: 500 });
          });
        } else if (status === 900) {
          httpReject({ code: -1, note: "登录过期，请重新登录！", data: {}, responseStatusCode: status });
        } else {
          httpReject({ code: -1, note: "系统异常，请稍候重试！", data: {}, responseStatusCode: status });
        }
      });
    }).then((rs) => {
      resolve(rs);
    }).catch(rs => {
      if (rs instanceof Error) {
        reject({
          code: -1,
          note: `异常,err=${JSON.stringify(rs, Object.getOwnPropertyNames(rs), 2)}`,
          data: {},
          responseStatusCode: 999,
        });
      } else {
        reject(rs);
      }
    });
  });
};
