import { writeFileSync, readFileSync, unlinkSync, symlinkSync, existsSync } from 'fs'
import { spawn } from 'child_process'
import { version } from './package.json'
import { askSingle } from 'reloquent'

const current = `target/closure-stylesheets-${version}-SNAPSHOT.jar`

const rr = (file, version, next) => {
  let index = readFileSync(file, 'utf8')
  index = index.replace(new RegExp(version, 'g'), next)
  writeFileSync(file, index)
}

;(async () => {
  const next = await askSingle(`What is the next version after ${version}?`)

  rr('package.json', version, next)
  rr('index.js', version, next)
  rr('pom.xml', version, next)
  const p = spawn('mvn', ['compile', 'assembly:single'], {
    stdio: 'inherit',
  })
  p.on('exit', () => {
    try {
      if (existsSync(current)) unlinkSync(current)
      unlinkSync('closure-stylesheets.jar')
      symlinkSync(`target/closure-stylesheets-${next}-SNAPSHOT-jar-with-dependencies.jar`, 'closure-stylesheets.jar')
    } catch (err) {
      console.log(err.message)
    }
  })
})()