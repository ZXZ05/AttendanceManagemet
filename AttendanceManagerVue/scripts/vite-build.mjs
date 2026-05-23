import { createRequire, syncBuiltinESMExports } from 'node:module'

const require = createRequire(import.meta.url)
const childProcess = require('node:child_process')
const originalExec = childProcess.exec

function isNetUse(command) {
  return typeof command === 'string' && command.trim().toLowerCase() === 'net use'
}

childProcess.exec = function patchedExec(command, options, callback) {
  if (!isNetUse(command)) {
    return originalExec.call(this, command, options, callback)
  }

  try {
    return originalExec.call(this, command, options, callback)
  } catch (error) {
    if (error && error.code === 'EPERM') {
      const cb = typeof options === 'function' ? options : callback
      if (typeof cb === 'function') {
        cb(error, '', '')
      }
      return {
        on() {
          return this
        },
        once() {
          return this
        },
        kill() {
          return false
        }
      }
    }
    throw error
  }
}

syncBuiltinESMExports()

const { build } = await import('vite')
await build()
