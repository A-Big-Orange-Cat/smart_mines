// https://github.com/michael-ciniawsky/postcss-load-config

module.exports = {
  "plugins": {
    "postcss-import": {},
    "postcss-url": {},
    // to edit target browsers: use "browserslist" field in package.json
    "autoprefixer": {},
    'postcss-px2rem-exclude': {
      remUnit: 192,
      propList: ['*'],
      exclude: /node_modules/i // 忽略node_modules目录下的文件
    }
  }
}
