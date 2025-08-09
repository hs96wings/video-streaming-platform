module.exports = {
  root: true, // 상위 폴더 ESlint 설정 무시
  env: {
    browser: true,
    es2021: true,
    node: true,
  },
  parser: 'vue-eslint-parser', // Vue 파일 인식
  parserOptions: {
    parser: '@typescript-eslint/parser', // TS 파서 연결
    ecmaVersion: 'latest',
    sourceType: 'module',
    project: './tsconfig.json',
    tsconfigRootDir: __dirname,
  },
  plugins: [
    'vue',
    '@typescript-eslint',
    'prettier', // prettier 규칙 연동
  ],
  extends: [
    'eslint:recommended',
    'plugin:vue/vue3-recommended',
    'plugin:@typescript-eslint/recommended',
    'plugin:prettier/recommended', // prettier 규칙 연동
  ],
  rules: {
    'prettier/prettier': 'warn', // prettier 위반 시 경고
  },
  overrides: [
    {
      files: ['*.js'],
      parserOptions: {
        project: null, // js 파일은 타입 검사 안 함
      },
    },
  ],
};
