
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Movie Catalog Report</title>
    <style>
        body { font-family: -apple-system, Segoe UI, Roboto, Arial, sans-serif;
               margin: 2rem; color: #222; }
        h1   { margin-bottom: .25rem; }
        .meta { color: #666; font-size: .9rem; margin-bottom: 1.5rem; }
        table { border-collapse: collapse; width: 100%; }
        th, td { border: 1px solid #ddd; padding: .5rem .75rem; text-align: left;
                 vertical-align: top; }
        th   { background: #f4f4f4; }
        tr:nth-child(even) td { background: #fafafa; }
        .score { font-variant-numeric: tabular-nums; white-space: nowrap; }
        .num   { text-align: right; font-variant-numeric: tabular-nums; }
        .empty { color: #999; font-style: italic; }
    </style>
</head>
<body>
    <h1>Movie Catalog Report</h1>
    <div class="meta">Generated at ${generatedAt} &middot; ${movies?size} movie<#if movies?size != 1>s</#if></div>

    <#if movies?size == 0>
        <p class="empty">No movies in the database.</p>
    <#else>
        <table>
            <thead>
                <tr>
                    <th>#</th>
                    <th>Title</th>
                    <th>Release Date</th>
                    <th class="num">Duration (min)</th>
                    <th class="score">Score</th>
                    <th>Genre</th>
                    <th>Actors</th>
                </tr>
            </thead>
            <tbody>
                <#list movies as m>
                <tr>
                    <td class="num">${m.movieId}</td>
                    <td>${m.title}</td>
                    <td>${(m.releaseDate!"-")?string}</td>
                    <td class="num">${m.duration}</td>
                    <td class="score">${m.score?string["0.0"]}</td>
                    <td>${(m.genre)!"-"}</td>
                    <td>
                        <#if (m.actors)?? && m.actors?length gt 0>
                            ${m.actors}
                        <#else>
                            <span class="empty">(no actors listed)</span>
                        </#if>
                    </td>
                </tr>
                </#list>
            </tbody>
        </table>
    </#if>
</body>
</html>
