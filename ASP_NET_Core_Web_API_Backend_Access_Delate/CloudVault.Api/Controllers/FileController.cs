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
        DeleteFileRequest request
    )
    {
        try
        {
            var deleteParams =
                new DeletionParams(request.PublicId);

            var result =
                await _cloudinaryService.Cloudinary
                    .DestroyAsync(deleteParams);

            if (result.Result == "ok")
            {
                return Ok(new
                {
                    message = "File deleted successfully."
                });
            }

            return BadRequest(new
            {
                message = result.Result
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