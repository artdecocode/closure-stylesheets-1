import makeTestSuite from '@zoroaster/mask'
import TempContext from 'temp-context'

export default makeTestSuite('test/result/', {
  context: TempContext,
  fork: {
    module: 'test/fixture/fork',
    /**
     * @param {string[]} args
     * @param {TempContext} t
     */
    async getArgs(args, { write }) {
      const src = await write('input.css', this.input)
      return [src, '--pretty-print']
    },
  },
  splitRe: /^\/\/ /gm,
})