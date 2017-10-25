var gulp = require('gulp');
var concat = require('gulp-concat');
var rename = require('gulp-rename');
var uglify = require('gulp-uglify');
var inject = require('gulp-inject');
var templateCache = require('gulp-angular-templatecache');

var newname = 'all.min.' + Date.now() + '.js';
var templatename = 'template.' + Date.now() + '.js';

gulp.task('scripts', function () {
    var sources = [
        'src/main/webapp/libs/jquery/dist/jquery.js',
        'src/main/webapp/libs/popper.js/dist/umd/popper.js',
        'src/main/webapp/libs/bootstrap/dist/js/bootstrap.js',
        'src/main/webapp/libs/angular/angular.js',
        'src/main/webapp/libs/angular-ui-router/release/angular-ui-router.js',

        'src/main/webapp/app/main/**/*.js',
        'src/main/webapp/app/pay/**/*.js'
    ];

    return gulp.src(sources)
        .pipe(concat(newname))
        .pipe(uglify())
        .pipe(gulp.dest('src/main/webapp/app/'));
});

gulp.task('inject-js', ['template-cache', 'scripts'], function () {
    gulp.src('src/main/webapp/content/template.html')
        .pipe(inject(gulp.src(['src/main/webapp/app/' + newname, 'src/main/webapp/app/' + templatename], {read: false}), {ignorePath: 'src/main/webapp/', addRootSlash: false}))
        .pipe(rename('index.html'))
        .pipe(gulp.dest('src/main/webapp/'));
});

gulp.task('template-cache', function () {
    return gulp.src('src/main/webapp/app/**/*.html')
        .pipe(templateCache(templatename, {
            module: 'ripayApp',
            root: 'app/'
        }))
        .pipe(gulp.dest('src/main/webapp/app/'));
});

gulp.task('default', ['template-cache', 'scripts', 'inject-js']);