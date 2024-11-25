# Releasing your library

You've written your library, created examples, and tested it in Processing. Now you are ready to release 
and publish it.


## Creating your Documentation Website
To have your library listed on the Processing website and Contribution Manager, you need to create a  documentation website and host it at a stable URL. This library template includes a built-in way to create and publish a website for your library using [Material for Mkdocs](https://squidfunk.github.io/mkdocs-material/) and Github Pages.

### Configuring Your Documentation Website
Customize your website by updating the `mkdocs.yml` file. We have included some plugins, such as search, code block copy and annotations, and navigation bars. The navigation menu is configured in `mkdocs.yml` as well.

To write your documentation, edit the Markdown files located in the `docs` folder. You can add new pages by creating additional Markdown files in the same folder, then add them to the menu in the `nav` section of the `mkdocs.yml` file.

For examples of how Processing libraries use Material for MkDocs, see [EasyEase](https://github.com/fredegd/EasyEase/blob/main/mkdocs.yml) or [FIP](https://github.com/prontopablo/FIP/blob/main/mkdocs.yml)

### Deploying your Documentation on Github Pages

This template include a workflow that automatically deploys your documentaion website to GitHub Pages. Here is how you can set this up for your repository:

1. Enable GitHub Pages and GitHub Actions in your repository settings: Under **Settings > GitHub Pages**, set the source to "Deploy from a branch", choose the `gh-pages` branch, select the `/(root)` folder, and then click **Save**.
2. Wait for the workflow to complete (this can take a few minutes). Once finished, your site’s URL will be displayed with the message "Your site is live at {{URL}}."
3. Add your website URL to the `url` property in the `release.properties` file.

Any time you want to update the documentation, edit your `.md` files and `mkdocs.yml` as needed, then push your changes to `main`. This will trigger the workflow to deploy an update.

_Note: You can find the workflow file at `.github/workflows/deploy_website.yml`._

### Alternative Hosting and Static Site Generators
If you prefer not to use the built-in workflow with GitHub Pages and Material for MkDocs, you are free to use another static site generator or hosting service. Note that it’s important that the site remains online, as it serves as a reference for users of your library. 

## Releasing on Github
Releasing your library on GitHub allows users to access known-working versions of your library. Unlike the repository itself, which may contain ongoing development or experimental features, releases provide specific versions of your library that are ready for use. Here is how you can make a new release using this template:

1. In your repository, click on **Releases**.
2. Click **Draft a new release** to start.
3. Click on **Choose a tag** a tag starting with "v" (e.g., `v1.0.0`). Make sure it matches the version set in your `release.properties` file.
4. Select the branch you want to use for this release (e.g., `main`).
5. Add a title and description for the release, highlighting key updates or changes.
6. Click on **Publish release**. 

This will trigger the GitHub workflow (`.github/workflows/release.yml`), which automatically create release artifacts—`*.txt`, `*.zip`, `*.pdex` files—and add them to the release.

_Note: By default, GitHub will also include compressed versions of your source code (e.g., `Source code (zip)` and `Source code (tar.gz)`)._

For more details on releases, see GitHub’s [official guide on releasing projects](https://docs.github.com/en/repositories/releasing-projects-on-github).

## Publish your Library to the Contribution Manager
Once you are ready to add your contribution to the Contribution Manager, email [contributions@processing.org](mailto:contributions@processing.org) with the url to your releases page. It should look something like `https://github.com/{{username}}/{{repository}}/releases/`. A Processing maintainer will review and publish your contribution. You may be asked for more information about your contribution or some modifications if needed.