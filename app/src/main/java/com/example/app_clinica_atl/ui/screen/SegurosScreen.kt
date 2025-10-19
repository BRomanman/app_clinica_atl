package com.example.atl_app.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.atl_app.R
import com.example.atl_app.navigation.Route

data class Seguro(val area: String, val nombre: String, val descripcion: String, val imagen: Int)

@Composable
fun SegurosScreen(navController: NavController) {
    val seguros = listOf(
        Seguro("Salud", "Seguro de Salud Básico", "Plan económico. Cubre consultas y medicamentos esenciales.", R.drawable.seguro_salud_2),
        Seguro("Salud", "Seguro de Salud Avanzado", "Cobertura extendida con especialistas, chequeos preventivos y urgencias.", R.drawable.seguro_salud_1),
        Seguro("Salud", "Seguro de Salud Premium", "Cobertura completa. Incluye consultas, hospitalización y emergencias.", R.drawable.seguro_3),
        Seguro("Salud", "Seguro de Salud Empresarial", "Plan para empleados con atención médica completa y programas de bienestar.", R.drawable.seguro_empresarial3),

        Seguro("Vida", "Seguro de Vida Individual", "Protección adaptada a necesidades individuales.", R.drawable.seguro_vida_2),
        Seguro("Vida", "Seguro de Vida Individual Premium", "Protección adaptada a todas las necesidades individuales.", R.drawable.seguro_vida_1),
        Seguro("Vida", "Seguro de Vida Familiar", "Cobertura completa en caso de fallecimiento.", R.drawable.familia_feliz1),
        Seguro("Vida", "Seguro de Vida Estudiante", "Protección económica adaptada a jóvenes y estudiantes, fácil de contratar.", R.drawable.clinica_1),
        Seguro("Vida", "Seguro de Vida Senior", "Plan pensado para adultos mayores, con cobertura en caso de fallecimiento y asistencia médica.", R.drawable.seguro_salud_3),
        Seguro("Vida", "Seguro de Vida Senior Premium", "Cobertura total para adultos mayores, permite disfrutar de la etapa final de la vida con tranquilidad.", R.drawable.atencion_1)
    )

    val grupos = seguros.groupBy { it.area }

    Box(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
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
                grupos.forEach { (area, lista) ->
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
                                    navController.navigate(Route.Seguros2.path) // Navegar a la pantalla de formulario
                                },
                            elevation = CardDefaults.cardElevation(6.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.9f)
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
                                    text = seguro.descripcion,
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



@Preview(showBackground = true, apiLevel = 34)
@Composable
fun SegurosScreenPreview() { SegurosScreen(navController = rememberNavController()) }