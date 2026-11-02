const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const CopyWebpackPlugin = require('copy-webpack-plugin');

module.exports = {
  // Точка входа приложения
  entry: './src/index.js',
  
  // Настройки вывода собранного файла
  output: {
    filename: 'app.js',
    path: path.resolve(__dirname, 'dist'),
    clean: true, // Очищает папку dist перед каждой сборкой
  },
  
  plugins: [
    // Копирование index.html в корень папки dist и автоматическое подключение app.js
    new HtmlWebpackPlugin({
      template: './src/index.html',
      filename: 'index.html',
    }),
    
    // Копирование стилей из ui/src/style.css в ui/dist/css/style.css
    new CopyWebpackPlugin({
      patterns: [
        { from: 'src/style.css', to: 'css' },
      ],
    }),
  ],
  mode: 'production', // Режим сборки для оптимизации
};
