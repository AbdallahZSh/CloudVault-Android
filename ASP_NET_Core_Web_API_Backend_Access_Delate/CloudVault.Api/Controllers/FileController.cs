using CloudVault.Api.DTOs;
using CloudVault.Api.Services;
using CloudinaryDotNet.Actions;
using Microsoft.AspNetCore.Mvc;

namespace CloudVault.Api.Controllers;

[ApiController]
[Route("api/files")]
public class FileController : ControllerBase
{
    private readonly CloudinaryService _cloudinaryService;

    public FileController(
        CloudinaryService cloudinaryService
    )
    {
        _cloudinaryService = cloudinaryService;
    }

    [HttpDelete]
    public async Task<IActionResult> DeleteFile(
        [FromBody] DeleteFileRequest request
    )
    {
        if (request == null || string.IsNullOrWhiteSpace(request.PublicId))
        {
             return BadRequest("PublicId is required.");
        }
        try
        {
            var deleteParams =
                new DeletionParams(request.PublicId);

            var result =
                await _cloudinaryService.Cloudinary
                    .DestroyAsync(deleteParams);

            Console.WriteLine($"Result: {result.Result}");
            Console.WriteLine($"Error: {result.Error?.Message}");

            Console.WriteLine($"PublicId: {request.PublicId}");
            if (result.Result == "ok")
            {
                return Ok(new
                {
                    message = "File deleted successfully."
                });
            }

        return BadRequest(new
        {
            result = result.Result,
            error = result.Error?.Message
        });
        }
        catch (Exception ex)
        {
            return StatusCode(500, new
            {
                message = ex.Message
            });
        }
    }
}