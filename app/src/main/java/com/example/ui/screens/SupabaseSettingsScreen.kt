package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Dataset
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.LikeGreen
import com.example.ui.viewmodel.RecipeViewModel

@Composable
fun SupabaseSettingsScreen(
    viewModel: RecipeViewModel,
    modifier: Modifier = Modifier
) {
    val likedList by viewModel.likedRecipes.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // Title
        Text(
            text = "Sistem & Supabase Mimarisi ⚙",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp
            )
        )
        Text(
            text = "Veritabanı Senkronizasyonu & RLS SQL Bilgisi",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Card 1: Live Status & Room DB Stats
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = LikeGreen, shape = CircleShape, modifier = Modifier.size(36.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.CloudDone, contentDescription = null, tint = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Veritabanı Durumu", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Yerel Room DB + Supabase Cloud Sync Aktif", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Kaydedilen Tarifler", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${likedList.size} Adet", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = FlameOrange)
                    }
                    Column {
                        Text("API Servis Kaynağı", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("TheMealDB v1 REST", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = LikeGreen)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Card 2: Supabase SQL Schema (BÖLÜM A Preview)
        Text(
            text = "Supabase SQL Tablo & RLS Kodları",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))

        val sqlSnippet = """
-- BÖLÜM A: Supabase SQL Mimarisi & RLS
CREATE TABLE IF NOT EXISTS public.users (
  id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  email TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS public.liked_recipes (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
  recipe_id TEXT NOT NULL,
  recipe_title TEXT NOT NULL,
  recipe_image TEXT,
  liked_at TIMESTAMPTZ DEFAULT NOW()
);

-- Row Level Security (RLS)
ALTER TABLE public.liked_recipes ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Kullanıcı kendi beğenilerini görebilir" 
  ON public.liked_recipes FOR SELECT 
  USING (auth.uid() = user_id);

CREATE POLICY "Kullanıcı beğeni ekleyebilir" 
  ON public.liked_recipes FOR INSERT 
  WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Kullanıcı beğeni silebilir" 
  ON public.liked_recipes FOR DELETE 
  USING (auth.uid() = user_id);
        """.trimIndent()

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Code, contentDescription = null, tint = FlameOrange)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("schema.sql", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = sqlSnippet,
                    color = Color(0xFF00FFC6),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Card 3: App Details
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Geliştirici & Mimar", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Kullanıcı: Eymen | Proje: RecipeSwipe MVP", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Mimari: Clean Architecture + Jetpack Compose + Room", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.loadDeck() },
                    colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reload_all_data_button")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tüm Verileri & Kart Desteğini Yenile")
                }
            }
        }
    }
}
