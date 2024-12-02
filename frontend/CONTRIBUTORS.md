# Contributors Guide

Welcome to our project! This guide outlines our team's coding standards and linting rules to ensure code quality and consistency throughout our codebase.

## ESLint Configuration

Our project uses ESLint to enforce coding standards. Below you will find a detailed explanation of our linting rules and the rationale behind them.

### General Rules

- **Environment**: The code is expected to run in a browser and supports ES2020 features.
- **Extends**:
  - `eslint:recommended`: Includes a set of recommended ESLint rules.
  - `plugin:@typescript-eslint/recommended`: Applies TypeScript-specific linting rules.
  - `plugin:react-hooks/recommended`: Enforces rules of hooks in React.
  - `plugin:prettier/recommended`: Integrates Prettier for code formatting.

### Plugins

- **react-refresh**: Ensures only components are exported, facilitating React Fast Refresh.
- **mui-path-imports**: Enforces specific import paths for MUI components to reduce bundle size.
- **unicorn**: Provides additional rules for improving code quality and consistency.

### Specific Rules

- **Prettier Integration**: We use Prettier for code formatting. The configuration enforces single quotes and handles line endings automatically for cross-platform compatibility.
- **File Naming**: Filenames should use `kebab-case` except for files in the `dist` directory.
- **React Specific**:
  - **Destructuring**: Automatic destructuring is disabled to simplify variable additions.
  - **Default Props**: Not required to define all props, allowing them to be `undefined`.

### Overrides

For TypeScript, JavaScript, and JSX files, we apply additional rules:

- **TypeScript**:
  - **Consistent Type Imports**: Enforces the use of `import type` where necessary to distinguish type imports from regular imports.
- **Import/Export Sorting**:
  - Managed by `eslint-plugin-simple-import-sort`, simplifies import/export statements and ensures consistency.
- **Tailwind CSS**:
  - Enforces the order of Tailwind CSS classes as per official sorting.

### Unused Code and Imports

- **No Unused Variables**: Prevents clutter by disallowing unused variables, with exceptions for prefixed variables (e.g., `_unusedVar`).

## Contributing

When contributing to the project, please make sure to:

- Run ESLint before submitting a pull request.
- Follow the defined linting rules to maintain code consistency.
- Update this guide if you introduce changes to the linting configuration.

Your contributions and adherence to these guidelines help us maintain a clean, efficient, and high-quality codebase. Thank you for your cooperation!
