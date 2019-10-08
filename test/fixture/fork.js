const closureStylesheets = require('../..')
const { spawn } = require('child_process')

const [,,...args] = process.argv

spawn('java', ['-jar', closureStylesheets, ...args], {
  stdio: 'inherit',
})