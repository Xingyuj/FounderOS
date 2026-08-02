from pydantic_settings import BaseSettings, SettingsConfigDict

class Settings(BaseSettings):
    postgres_host: str = "localhost"
    postgres_port: int = 5432
    postgres_db: str = "founder_os"
    postgres_user: str = "founder_os"
    postgres_password: str = "founder_os"
    orchestrator_port: int = 8000
    model_config = SettingsConfigDict(env_file="../.env", extra="ignore")

    @property
    def database_uri(self) -> str:
        return f"postgresql://{self.postgres_user}:{self.postgres_password}@{self.postgres_host}:{self.postgres_port}/{self.postgres_db}"

