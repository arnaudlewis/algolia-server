var gulp = require('gulp');
var sass = require('gulp-sass');
var autoprefixer = require('gulp-autoprefixer');
var rename = require('gulp-rename');
var plumber = require('gulp-plumber');

const Path = {
  src: './app/assets/stylesheets/**/*.sass',
  entryPoint: './app/assets/stylesheets/main.sass',
  output: './public/stylesheets/compiled/'
}

gulp.task('build-style', function () {
  var options = {
    outputStyle: 'compressed'
  };
  gulp.src(Path.entryPoint)
    .pipe(plumber())
    .pipe(sass(options).on('error', sass.logError))
    .pipe(plumber.stop())
    .pipe(autoprefixer({
      browsers: [
          '> 1%',
          'last 2 versions',
          'firefox >= 4',
          'safari 7',
          'safari 8',
          'IE 8',
          'IE 9',
          'IE 10',
          'IE 11'
      ],
      cascade: false
  }))
  .pipe(gulp.dest(Path.output))
});

gulp.task('watch-style', function() {
    gulp.watch([Path.src], ['build-style']);
});

gulp.task('build', ['build-style']);

gulp.task('default', ['build-style', 'watch-style']);
