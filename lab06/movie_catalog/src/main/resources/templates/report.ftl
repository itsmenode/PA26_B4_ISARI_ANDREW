<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Movie Report</title>
    <style>
        body        { font-family: Arial, sans-serif; margin: 2em; }
        h1          { color: #2c3e50; }
        table       { border-collapse: collapse; width: 100%; }
        th, td      { border: 1px solid #bdc3c7; padding: 8px 12px; text-align: left; }
        th          { background-color: #34495e; color: white; }
        tr:nth-child(even) { background-color: #ecf0f1; }
        .meta       { color: #7f8c8d; font-size: 0.9em; margin-bottom: 1.5em; }
    </style>
</head>
<body>
<h1>Movie Report</h1>
<p class="meta">Generated at ${generatedAt}</p>

<table>
    <thead>
    <tr>
        <th>#</th>
        <th>Title</th>
        <th>Release date</th>
        <th>Duration (min)</th>
        <th>Score</th>
        <th>Genre</th>
        <th>Actors</th>
    </tr>
    </thead>
    <tbody>
    <#list movies as m>
        <tr>
            <td>${m.movieId}</td>
            <td>${m.title}</td>
            <td>${(m.releaseDate)!"—"}</td>
            <td>${m.duration}</td>
            <td>${m.score}</td>
            <td>${(m.genre)!"—"}</td>
            <td>${(m.actors)!""}</td>
        </tr>
    </#list>
    </tbody>
</table>
</body>
</html>
