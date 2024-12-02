module.exports = {
  root: true,
  env: { browser: true, es2020: true },
  extends: [
    'eslint:recommended',
    'plugin:@typescript-eslint/recommended',
    'plugin:react-hooks/recommended',
    'plugin:prettier/recommended',
  ],
  ignorePatterns: ['dist', '.eslintrc.cjs','src/components/ui'],
  parser: '@typescript-eslint/parser',
  plugins: ['react-refresh', 'mui-path-imports', 'unicorn'],
  rules: {
    'react-refresh/only-export-components': [
      'warn',
      { allowConstantExport: true },
    ],
    'mui-path-imports/mui-path-imports': 'error',
    'prettier/prettier': [
      'error',
      {
        singleQuote: true,
        endOfLine: 'auto',
      },
    ],
    'unicorn/filename-case': [
      'error',
      {
        case: 'kebabCase',
        ignore: ['/dist', 'App*'],
      },
    ],
  },
  overrides: [    {
    files: ['**/*.ts', '**/*.tsx', '**/*.js'],
    plugins: [
      '@typescript-eslint',
      'unused-imports',
      'tailwindcss',
      'simple-import-sort',
    ],
    extends: [
      'plugin:tailwindcss/recommended',
      'plugin:prettier/recommended',
    ],
    rules: {
      'prettier/prettier': [
        'error',
        {
          singleQuote: true,
          endOfLine: 'auto',
        },
      ],
      'max-params': ['error', 3], // Limit the number of parameters in a function to use object instead
      'react/destructuring-assignment': 'off', // Vscode doesn't support automatically destructuring, it's a pain to add a new variable
      'react/require-default-props': 'off', // Allow non-defined react props as undefined
      '@typescript-eslint/comma-dangle': 'off', // Avoid conflict rule between Eslint and Prettier
      '@typescript-eslint/consistent-type-imports': 'error', // Ensure `import type` is used when it's necessary
      'import/prefer-default-export': 'off', // Named export is easier to refactor automatically
      'react-refresh/only-export-components': 'off', // Allow to export multiple components in a single file
      'tailwindcss/classnames-order': [
        'warn',
        {
          officialSorting: true,
        },
      ], // Follow the same ordering as the official plugin `prettier-plugin-tailwindcss`
      'simple-import-sort/imports': 'error', // Import configuration for `eslint-plugin-simple-import-sort`
      'simple-import-sort/exports': 'error', // Export configuration for `eslint-plugin-simple-import-sort`
      '@typescript-eslint/no-unused-vars': 'off',
      'tailwindcss/no-custom-classname': 'off',
      'unused-imports/no-unused-imports': 'error',
      'max-lines-per-function': 'off',
      'unused-imports/no-unused-vars': [
        'error',
        {
          argsIgnorePattern: '^_',
          varsIgnorePattern: '^_',
          caughtErrorsIgnorePattern: '^_',
        },
      ],
    },
  },],
}
