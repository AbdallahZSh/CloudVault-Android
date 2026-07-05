using CloudinaryDotNet;
using CloudVault.Api.Configuration;

namespace CloudVault.Api.Services;

public class CloudinaryService
{
    public Cloudinary Cloudinary { get; }

    public CloudinaryService(CloudinarySettings settings)
    {
        var account = new Account(
            settings.CloudName,
            settings.ApiKey,
            settings.ApiSecret
        );

        Cloudinary = new Cloudinary(account);
    }
}