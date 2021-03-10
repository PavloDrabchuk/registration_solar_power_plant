function jump(id) {
    const top = document.getElementById(id).offsetTop;
    const headerHeight = document.getElementById('header').offsetHeight;

    window.scrollTo(0, top - headerHeight);
}