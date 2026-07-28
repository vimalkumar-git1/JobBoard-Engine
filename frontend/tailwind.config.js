/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,jsx}",
  ],
  theme: {
    extend: {
      colors: {
        navy: "#1F3864",
        accent: "#2E5395",
        surface: "#F7F9FC",
      },
    },
  },
  plugins: [],
}
