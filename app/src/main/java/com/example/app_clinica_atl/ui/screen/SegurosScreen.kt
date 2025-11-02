package com.example.app_clinica_atl.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.app_clinica_atl.R
import com.example.app_clinica_atl.navigation.Route

data class Seguro(
    val area: String,
    val nombre: String,
    val descripción: String,
    val imagen: Int
)

@Composable
fun SegurosScreen(navController: NavController) {
    val seguros = listOf(
        Seguro(
            "Salud",
            "Seguro de Salud Básico",
            "Plan económico con cobertura de consultas y medicamentos esenciales.",
            R.drawable.seguro_salud_2
        ),
        Seguro(
            "Salud",
            "Seguro de Salud Avanzado",
            "Cobertura extendida con especialistas, chequeos preventivos y urgencias.",
            R.drawable.seguro_salud_1
        ),
        Seguro(
            "Salud",
            "Seguro de Salud Premium",
            "Cobertura completa que incluye hospitalización y atención de urgencia.",
            R.drawable.seguro_3
        ),
        Seguro(
            "Salud",
            "Seguro de Salud Empresarial",
            "Plan orientado a colaboradores con programas de bienestar y chequeos periodicos.",
            R.drawable.seguro_empresarial3
        ),
        Seguro(
            "Vida",
            "Seguro de Vida Individual",
            "Protección pensada para quienes buscan un plan base y flexible.",
            R.drawable.seguro_vida_2
        ),
        Seguro(
            "Vida",
            "Seguro de Vida Individual Premium",
            "Cobertura total con beneficios adicionales para toda la familia.",
            R.drawable.seguro_vida_1
        ),
        Seguro(
            "Vida",
            "Seguro de Vida Familiar",
            "Protege a tu familia ante eventualidades y asegura estabilidad económica.",
            R.drawable.familia_feliz1
        ),
        Seguro(
            "Vida",
            "Seguro de Vida Estudiante",
            "Pensado para estudiantes y jovenes adultos con cuotas accesibles.",
            R.drawable.clinica_1
        ),
        Seguro(
            "Vida",
            "Seguro de Vida Senior",
            "Cobertura enfocada en adultos mayores, con asistencia médica incluida.",
            R.drawable.seguro_salud_3
        ),
        Seguro(
            "Vida",
            "Seguro de Vida Senior Premium",
            "Plan integral con acompanamiento permanente y servicios domiciliarios.",
            R.drawable.atencion_1
        )
    )

    val segurosAgrupados = seguros.groupBy { it.area }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Seguros Disponibles",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                segurosAgrupados.forEach { (area, lista) ->
                    item {
                        Text(
                            text = area,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }

                    items(lista) { seguro ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navController.navigate(Route.InsuranceForm.path) {
                                        launchSingleTop = true
                                    }
                                },
                            elevation = CardDefaults.cardElevation(6.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.background
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = seguro.imagen),
                                    contentDescription = seguro.nombre,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(150.dp),
                                    contentScale = ContentScale.Crop
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = seguro.nombre,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = seguro.descripción,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    textAlign = TextAlign.Start
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SegurosScreenPreview() {
    SegurosScreen(navController = rememberNavController())
}
