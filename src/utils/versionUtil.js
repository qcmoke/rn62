/**
 * 判断两个版本字符串的大小
 * @param  {string} v1 原始版本
 * @param  {string} v2 要求最小版本
 * @return {number}    小于最小版本，则返回负数，大于等于则返回0
 */
export const compareVersion = (v1, v2) => {
  let sources = v1.split(".");
  let dests = v2.split(".");
  let maxL = Math.max(sources.length, dests.length);
  let result = 0;
  for (let i = 0; i < maxL; i++) {
    let preValue = sources.length > i ? sources[i] : 0;
    let preNum = isNaN(Number(preValue)) ? preValue.charCodeAt() : Number(preValue);
    let lastValue = dests.length > i ? dests[i] : 0;
    let lastNum = isNaN(Number(lastValue)) ? lastValue.charCodeAt() : Number(lastValue);
    if (preNum < lastNum) {
      result = -1;
      break;
    } else if (preNum > lastNum) {
      result = 0;
      break;
    }
  }
  return result;
};
