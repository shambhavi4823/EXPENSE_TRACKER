let myChart;

async function updateChart() {
    const response = await fetch('/ExpenseTracker/getChartData'); // Servlet URL
    const data = await response.json();

    const ctx = document.getElementById('myPieChart').getContext('2d');

    if (myChart) myChart.destroy(); // Purana chart delete karo naya banane se pehle

    myChart = new Chart(ctx, {
        type: 'pie',
        data: {
            labels: Object.keys(data),
            datasets: [{
                data: Object.values(data),
                backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0']
            }]
        }
    });
}

window.onload = updateChart;