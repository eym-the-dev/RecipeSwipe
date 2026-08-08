import { serve } from "https://deno.land/std@0.168.0/http/server.ts";

const corsHeaders = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type",
};

serve(async (req) => {
  if (req.method === "OPTIONS") {
    return new Response("ok", { headers: corsHeaders });
  }

  try {
    let category = "";
    let ingredient = "";
    let query = "";

    if (req.method === "POST") {
      const body = await req.json().catch(() => ({}));
      category = body.category || "";
      ingredient = body.ingredient || "";
      query = body.query || "";
    } else {
      const url = new URL(req.url);
      category = url.searchParams.get("category") || "";
      ingredient = url.searchParams.get("ingredient") || "";
      query = url.searchParams.get("query") || "";
    }

    let apiUrl = "https://www.themealdb.com/api/json/v1/1/random.php";

    if (query) {
      apiUrl = `https://www.themealdb.com/api/json/v1/1/search.php?s=${encodeURIComponent(query)}`;
    } else if (ingredient) {
      apiUrl = `https://www.themealdb.com/api/json/v1/1/filter.php?i=${encodeURIComponent(ingredient)}`;
    } else if (category && category !== "All") {
      apiUrl = `https://www.themealdb.com/api/json/v1/1/filter.php?c=${encodeURIComponent(category)}`;
    }

    const response = await fetch(apiUrl);
    if (!response.ok) {
      return new Response(
        JSON.stringify({ error: "TheMealDB API çağrısı başarısız oldu." }),
        { status: response.status, headers: { ...corsHeaders, "Content-Type": "application/json" } }
      );
    }

    const data = await response.json();
    const meals = data.meals || [];

    // Map meals into standardized Recipe structure
    const formattedRecipes = meals.map((m: any) => {
      const ingredients = [];
      for (let i = 1; i <= 20; i++) {
        const ing = m[`strIngredient${i}`];
        const measure = m[`strMeasure${i}`];
        if (ing && ing.trim().length > 0) {
          ingredients.push({
            name: ing.trim(),
            measure: measure ? measure.trim() : "İsteğe göre"
          });
        }
      }

      return {
        id: m.idMeal || String(Math.floor(Math.random() * 900000) + 100000),
        title: m.strMeal || "Lezzetli Yemek Tarifi",
        imageUrl: m.strMealThumb || "https://www.themealdb.com/images/media/meals/wyvxrw1511516212.jpg",
        category: m.strCategory || category || "Genel",
        area: m.strArea || "Uluslararası",
        instructions: m.strInstructions || "Malzemeleri hazırlayıp karıştırın ve kısık ateşte pişirin.",
        ingredients: ingredients.length > 0 ? ingredients : [{ name: "Taze Malzemeler", measure: "Yeteri Kadar" }],
        prepTimeMinutes: Math.floor(Math.random() * 35) + 15,
        calories: Math.floor(Math.random() * 450) + 250,
        difficulty: "EASY",
        isNutFree: true,
        isDairyFree: false,
        isVegetarian: m.strCategory === "Vegetarian",
        isVegan: m.strCategory === "Vegan",
        isGlutenFree: true,
        youtubeUrl: m.strYoutube || ""
      };
    });

    return new Response(
      JSON.stringify({ meals: formattedRecipes }),
      { status: 200, headers: { ...corsHeaders, "Content-Type": "application/json" } }
    );
  } catch (error: any) {
    return new Response(
      JSON.stringify({ error: error.message || "Sunucu hatası oluştu" }),
      { status: 500, headers: { ...corsHeaders, "Content-Type": "application/json" } }
    );
  }
});
