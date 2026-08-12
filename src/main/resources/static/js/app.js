// script.js
const API_BASE = "http://localhost:8080";
const api = axios.create({
    baseURL: API_BASE,
    headers: { "Content-Type": "application/json" }
});

// Funções auxiliares
function formatDate(dateString) {
    const d = new Date(dateString);
    return d.toLocaleDateString("pt-BR");
}
function isToday(dateString) {
    const today = new Date();
    const date = new Date(dateString);
    return date.getDate() === today.getDate() &&
           date.getMonth() === today.getMonth() &&
           date.getFullYear() === today.getFullYear();
}

// GET genérico
async function apiGet(endpoint) {
    try {
        const res = await api.get(endpoint);
        return res.data;
    } catch (err) {
        console.error(`Erro GET ${endpoint}:`, err);
        return null;
    }
}

// ========== DASHBOARD ==========
async function loadDashboard() {
    const orders = await apiGet("/orders");
    if (!orders) return;

    // Estatísticas
    const pedidosHoje = orders.filter(o => isToday(o.date)).length;
    const pendentes = orders.filter(o => o.status.toLowerCase() === "pendente").length;
    const concluidos = orders.filter(o => o.status.toLowerCase() === "concluído").length;

    document.querySelectorAll("#stats .stat-card p")[0].textContent = pedidosHoje;
    document.querySelectorAll("#stats .stat-card p")[1].textContent = pendentes;
    document.querySelectorAll("#stats .stat-card p")[2].textContent = concluidos;

    // Últimos pedidos
    const tbody = document.querySelector("#orders tbody");
    tbody.innerHTML = "";
    orders.slice(0, 10).forEach(order => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td>#${order.id}</td>
            <td>${order.clientName || "—"}</td>
            <td>${order.status}</td>
            <td>${formatDate(order.date)}</td>
            <td>R$ ${Number(order.total).toFixed(2).replace(".", ",")}</td>
            <td><a href="#" data-id="${order.id}" class="view-order">Ver</a></td>
        `;
        tbody.appendChild(tr);
    });
}

// ========== PEDIDOS ==========
async function loadPedidosPage() {
    const orders = await apiGet("/orders");
    if (!orders) return;
    const tbody = document.querySelector("#table-pedidos tbody");
    tbody.innerHTML = "";
    orders.forEach(order => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td>#${order.id}</td>
            <td>${order.clientName || "—"}</td>
            <td>${order.status}</td>
            <td>${formatDate(order.date)}</td>
            <td>R$ ${Number(order.total).toFixed(2).replace(".", ",")}</td>
            <td>
                <a href="#" class="view-order" data-id="${order.id}">Ver</a>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

// ========== CLIENTES ==========
async function loadClientesPage() {
    const users = await apiGet("/users");
    if (!users) return;
    const tbody = document.querySelector("#table-clientes tbody");
    tbody.innerHTML = "";
    users.forEach(user => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td>#${user.id}</td>
            <td>${user.name || "—"}</td>
            <td>${user.email || "—"}</td>
            <td>
                <a href="#" class="view-user" data-id="${user.id}">Ver</a>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

// Detecta qual página está carregada e executa a função certa
document.addEventListener("DOMContentLoaded", () => {
    if (document.querySelector("#stats")) loadDashboard();
    if (document.querySelector("#table-pedidos")) loadPedidosPage();
    if (document.querySelector("#table-clientes")) loadClientesPage();
});
