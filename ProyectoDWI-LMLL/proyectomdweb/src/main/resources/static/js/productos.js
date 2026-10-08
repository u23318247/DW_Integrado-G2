/* ===================================
   LÓGICA DE PRESENTACION DE PRODUCTOS
   =================================== */

document.addEventListener('DOMContentLoaded', () => {
    const generoSelect   = document.getElementById('filtro-genero');
    const categoriaLinks = document.querySelectorAll('.filtro-cat');
    const contenedor     = document.getElementById('contenedor-productos');

    let categoriaActual = "";

    const actualizarProductos = () => {
        const genero = generoSelect.value;
        const url    = `/productos/ajax?genero=${genero}&categoriaId=${categoriaActual}`;

        fetch(url)
            .then(response => response.text())
            .then(html => {
                contenedor.innerHTML = html;
            });
    };

    // Escucha cambio de Género
    generoSelect.addEventListener('change', actualizarProductos);

    // Escucha los clicks en Categorías
    categoriaLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            
            categoriaLinks.forEach(l => l.classList.remove('active'));
            link.classList.add('active');

            categoriaActual = link.getAttribute('data-id');
            actualizarProductos();
        });
    });
});