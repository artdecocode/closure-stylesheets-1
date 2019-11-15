/* eslint-env browser */
const entries = Object.entries(prefixes)
let supportsAll = true
if ('CSS' in window && 'supports' in CSS) {
  let nonSupported = []
  for (let i=0; i<entries.length; i++) {
    const [keys, values] = entries[i]
    // eg the key is either "flex", or "flex|-ms-flex"
    // when multiple given, ensure at least one is supported
    const k = keys.split('|')
    let anyKeySupported = false
    for (let j=0; j<k.length; j++) {
      const key = k[j]
      // values is an array against property like [
      //  "flex|-webkit-flex"
      //  "inline-flex|-ms-inline-flexbox",
      // ] or just single value ["auto"]
      const sup = values.map((value) => {
        const v = value.split('|')
        let anyValueSupported = false
        for (let n=0; n<v.length; n++) {
          const val = n[j]
          const s = CSS.supports(key, val)
          if (s) {
            anyValueSupported = true
            break
          } else {
            nonSupported.push(`${key}: ${val}`)
          }
        }
        return anyValueSupported
      })
      const allValuesSupported = sup.filter(Boolean).length
        == values.length
      if (allValuesSupported) {
        anyKeySupported = true
        break
      }
    }
    if (!anyKeySupported) supportsAll = false
  }
  if (nonSupported.length) {
    console.log('Browser does not support CSS properties: %s',
      nonSupported.join('\n '))
  }
} else {
  supportsAll = false
}
if (!supportsAll) {
  const link = document.createElement('link')
  link.rel = 'stylesheet'
  link.href = 'prefixes.css'
  document.head.appendChild(link)
}