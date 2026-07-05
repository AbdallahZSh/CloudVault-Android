using CloudVault.Api.Configuration;
using CloudVault.Api.Services;

var builder = WebApplication.CreateBuilder(args);
builder.Services.AddControllers();
// Add services to the container.
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();
//---------------------------------
builder.Services.Configure<CloudinarySettings>(
    builder.Configuration.GetSection("Cloudinary")
);

var settings = builder.Configuration
    .GetSection("Cloudinary")
    .Get<CloudinarySettings>()!;

builder.Services.AddSingleton(
    new CloudinaryService(settings)
);
//---------------------------------

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();

app.MapControllers();

app.Run();