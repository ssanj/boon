## Design Decisions ##


# Defer

- Use Defer on all internal parameters where you want to control evaluation. This makes it easier to locate where parameters need to be evaluated as needed.
- On public apis use => instead, because it makes it easier to use the API by not having to wrap everything in a Defer(() => ???)