import rollupProject from '@build/rollupProject';

export default rollupProject({
  main: {
    name: 'PlaystrategyStorm',
    input: 'src/main.ts',
    output: 'storm',
  },
});
