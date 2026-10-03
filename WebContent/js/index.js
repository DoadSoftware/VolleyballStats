let lastMatchUpdate = Date.now();

setInterval(function() {
    fetch(window.location.pathname.replace(/\/[^/]*$/, '') + '/match-status', {
        cache: 'no-store'
    })
    .then(response => response.text())
    .then(timestamp => {
        const currentMatchUpdate = Number(timestamp);
        if (currentMatchUpdate > 0 && currentMatchUpdate > lastMatchUpdate) {
            window.location.reload();
        }
    })
    .catch(function() {
    });
}, 2000);